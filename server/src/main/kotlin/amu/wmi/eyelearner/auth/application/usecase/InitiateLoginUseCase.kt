package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.domain.usos.exception.UsosApiException
import amu.wmi.eyelearner.auth.domain.usos.repository.OAuthRequestTokenStorage
import amu.wmi.eyelearner.auth.domain.usos.service.UsosOAuthClient
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.infrastructure.config.UsosProperties
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class InitiateLoginUseCase(
    private val usosOAuthClient: UsosOAuthClient,
    private val requestTokenStorage: OAuthRequestTokenStorage,
    private val usosProperties: UsosProperties
) {
    suspend fun execute(request: InitLoginRequest): InitLoginResponse {
        return try {
            val requestToken = usosOAuthClient.getRequestToken(
                callbackUrl = request.callbackUrl,
                scopes = usosProperties.scopes.ifBlank { null }
            )

            requestTokenStorage.storeRequestTokenSecret(
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
}
