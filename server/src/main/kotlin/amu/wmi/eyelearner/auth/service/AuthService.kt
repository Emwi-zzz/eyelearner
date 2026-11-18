package amu.wmi.eyelearner.auth.service

import amu.wmi.eyelearner.auth.contract.LoginContract
import amu.wmi.eyelearner.auth.contract.LogoutContract
import amu.wmi.eyelearner.auth.contract.RefreshSessionContract
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import amu.wmi.eyelearner.config.AuthProperties
import amu.wmi.eyelearner.config.UsosProperties
import amu.wmi.eyelearner.domain.token.repository.TokenStorage
import amu.wmi.eyelearner.domain.token.service.TokenGenerator
import amu.wmi.eyelearner.domain.user.model.User
import amu.wmi.eyelearner.domain.user.repository.UserRepository
import amu.wmi.eyelearner.infrastructure.usos.UsosApiException
import amu.wmi.eyelearner.infrastructure.usos.UsosOAuthClient
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val usosOAuthClient: UsosOAuthClient,
    private val userRepository: UserRepository,
    private val tokenStorage: TokenStorage,
    private val tokenGenerator: TokenGenerator,
    private val authProperties: AuthProperties,
    private val usosProperties: UsosProperties
) : LoginContract, LogoutContract, RefreshSessionContract {

    override suspend fun initiate(request: InitLoginRequest): InitLoginResponse {
        return try {
            val requestToken = usosOAuthClient.getRequestToken(
                callbackUrl = request.callbackUrl,
                scopes = usosProperties.scopes.ifBlank { null }
            )

            tokenStorage.storeRequestTokenSecret(
                oauthToken = requestToken.oauthToken,
                tokenSecret = requestToken.oauthTokenSecret,
                ttl = Duration.ofMinutes(15)
            )

            val authorizeUrl = "${usosProperties.baseUrl}/services/oauth/authorize?oauth_token=${requestToken.oauthToken}"
            InitLoginResponse.Success(
                authorizeUrl = authorizeUrl,
                oauthToken = requestToken.oauthToken
            )
        } catch (e: UsosApiException) {
            InitLoginResponse.Error("Failed to initiate login with USOS: ${e.message}")
        } catch (e: Exception) {
            InitLoginResponse.Error(e.message ?: "Unexpected error during login initialization")
        }
    }

    override suspend fun finalize(request: FinalizeLoginRequest): AuthSessionResponse {
        return try {
            val requestTokenSecret = tokenStorage.getAndRemoveRequestTokenSecret(request.oauthToken)
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
                expiresInSeconds = authProperties.accessTokenExpirationSeconds
            )
        } catch (e: Exception) {
            AuthSessionResponse.Error(e.message ?: "Unexpected error finalizing login")
        }
    }

    override suspend fun refresh(request: RefreshSessionRequest): AuthSessionResponse {
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
                expiresInSeconds = authProperties.accessTokenExpirationSeconds
            )
        } catch (e: Exception) {
            AuthSessionResponse.Error(e.message ?: "Unexpected error refreshing session")
        }
    }

    override suspend fun logout(request: LogoutRequest): LogoutResponse {
        return try {
            val record = tokenStorage.findRefreshToken(request.refreshToken)
            if (record != null) {
                record.associatedAccessToken?.let {
                    tokenStorage.revokeAccessToken(it)
                }
                tokenStorage.revokeRefreshToken(request.refreshToken)
            }
            LogoutResponse.Success
        } catch (e: Exception) {
            LogoutResponse.Error(e.message ?: "Unexpected error during logout")
        }
    }
}