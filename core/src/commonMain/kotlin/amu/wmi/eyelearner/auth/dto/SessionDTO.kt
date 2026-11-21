package amu.wmi.eyelearner.auth.dto

import amu.wmi.eyelearner.common.contract.ContractError
import kotlinx.serialization.Serializable

@Serializable
data class RefreshSessionRequest(
    val refreshToken: String
)

@Serializable
sealed interface AuthSessionResponse {
    @Serializable
    data class Success(
        val accessToken: String,
        val refreshToken: String,
        val expiresInSeconds: Long
    ) : AuthSessionResponse

    @Serializable
    data object InvalidOrExpiredToken : AuthSessionResponse, ContractError.Unauthorized {
        override val message: String = "Invalid or expired token"
    }

    @Serializable
    data class Error(
        override val message: String
    ) : AuthSessionResponse, ContractError.InternalError
}
