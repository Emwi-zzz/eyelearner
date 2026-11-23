package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.domain.token.repository.TokenStorage
import amu.wmi.eyelearner.auth.domain.token.service.TokenGenerator
import amu.wmi.eyelearner.auth.domain.usos.exception.UsosApiException
import amu.wmi.eyelearner.auth.domain.usos.repository.OAuthRequestTokenStorage
import amu.wmi.eyelearner.auth.domain.usos.service.UsosOAuthClient
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.infrastructure.config.AuthProperties
import amu.wmi.eyelearner.user.domain.model.User
import amu.wmi.eyelearner.user.domain.repository.UserRepository
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Component
class FinalizeLoginUseCase(
    private val requestTokenStorage: OAuthRequestTokenStorage,
    private val usosOAuthClient: UsosOAuthClient,
    private val userRepository: UserRepository,
    private val tokenStorage: TokenStorage,
    private val tokenGenerator: TokenGenerator,
    private val authProperties: AuthProperties
) {
    suspend fun execute(request: FinalizeLoginRequest): AuthSessionResponse {
        return try {
            val requestTokenSecret = requestTokenStorage.getAndRemoveRequestTokenSecret(request.oauthToken)
                ?: return AuthSessionResponse.InvalidOrExpiredToken

            val accessToken = try {
                usosOAuthClient.getAccessToken(
                    oauthToken = request.oauthToken,
                    oauthVerifier = request.oauthVerifier,
                    requestTokenSecret = requestTokenSecret
                )
            } catch (e: UsosApiException) {
                return AuthSessionResponse.InvalidOrExpiredToken
            }

            val userProfile = try {
                usosOAuthClient.getUserProfile(
                    accessToken = accessToken.oauthToken,
                    accessTokenSecret = accessToken.oauthTokenSecret
                )
            } catch (e: Exception) {
                return AuthSessionResponse.Error("Failed to fetch user profile from USOS: ${e.message}")
            }

            val user = userRepository.findByUsosId(userProfile.id)?.let { existing ->
                val updated = existing.copy(
                    firstName = userProfile.firstName,
                    lastName = userProfile.lastName,
                    updatedAt = Instant.now()
                )
                userRepository.save(updated)
            } ?: run {
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    usosId = userProfile.id,
                    firstName = userProfile.firstName,
                    lastName = userProfile.lastName,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                userRepository.save(newUser)
            }

            val appAccessToken = tokenGenerator.generateToken()
            val appRefreshToken = tokenGenerator.generateToken()
            val accessTtl = Duration.ofSeconds(authProperties.accessTokenExpirationSeconds)
            val refreshTtl = Duration.ofSeconds(authProperties.refreshTokenExpirationSeconds)

            tokenStorage.storeAccessToken(appAccessToken, user.id, accessTtl)
            tokenStorage.storeRefreshToken(appRefreshToken, user.id, appAccessToken, refreshTtl)

            AuthSessionResponse.Success(
                accessToken = appAccessToken,
                refreshToken = appRefreshToken,
                expiresInSeconds = authProperties.accessTokenExpirationSeconds,
                userId = user.id
            )
        } catch (e: Exception) {
            AuthSessionResponse.Error(e.message ?: "Unexpected error finalizing login")
        }
    }
}
