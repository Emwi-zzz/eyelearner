package amu.wmi.eyelearner.auth.application.usecase

import amu.wmi.eyelearner.auth.domain.token.repository.TokenStorage
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import org.springframework.stereotype.Component

@Component
class LogoutUseCase(
    private val tokenStorage: TokenStorage
) {
    suspend fun execute(request: LogoutRequest): LogoutResponse {
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
