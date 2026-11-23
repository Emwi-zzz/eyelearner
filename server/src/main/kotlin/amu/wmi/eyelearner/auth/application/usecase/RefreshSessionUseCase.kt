package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.domain.token.repository.TokenStorage
import amu.wmi.eyelearner.auth.domain.token.service.TokenGenerator
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import amu.wmi.eyelearner.auth.infrastructure.config.AuthProperties
import amu.wmi.eyelearner.user.domain.repository.UserRepository
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RefreshSessionUseCase(
    private val tokenStorage: TokenStorage,
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator,
    private val authProperties: AuthProperties
) {
    suspend fun execute(request: RefreshSessionRequest): AuthSessionResponse {
        return try {
            val refreshTokenRecord = tokenStorage.findRefreshToken(request.refreshToken)
                ?: return AuthSessionResponse.InvalidOrExpiredToken

            val user = userRepository.findById(refreshTokenRecord.userId)
                ?: return AuthSessionResponse.InvalidOrExpiredToken

            refreshTokenRecord.associatedAccessToken?.let {
                tokenStorage.revokeAccessToken(it)
            }
            tokenStorage.revokeRefreshToken(request.refreshToken)

            val newAccessToken = tokenGenerator.generateToken()
            val newRefreshToken = tokenGenerator.generateToken()
            val accessTtl = Duration.ofSeconds(authProperties.accessTokenExpirationSeconds)
            val refreshTtl = Duration.ofSeconds(authProperties.refreshTokenExpirationSeconds)

            tokenStorage.storeAccessToken(newAccessToken, user.id, accessTtl)
            tokenStorage.storeRefreshToken(newRefreshToken, user.id, newAccessToken, refreshTtl)

            AuthSessionResponse.Success(
                accessToken = newAccessToken,
                refreshToken = newRefreshToken,
                expiresInSeconds = authProperties.accessTokenExpirationSeconds,
                userId = user.id
            )
        } catch (e: Exception) {
            AuthSessionResponse.Error(e.message ?: "Unexpected error refreshing session")
        }
    }
}
