package amu.wmi.eyelearner.auth.dto

import amu.wmi.eyelearner.common.contract.ContractError
import kotlinx.serialization.Serializable

@Serializable
data class InitLoginRequest(
    val callbackUrl: String
)

@Serializable
sealed interface InitLoginResponse {
    @Serializable
    data class Success(
        val authorizeUrl: String,
        val oauthToken: String
    ) : InitLoginResponse

    @Serializable
    data class Error(
        override val message: String
    ) : InitLoginResponse, ContractError.BadGateway
}

@Serializable
data class FinalizeLoginRequest(
    val oauthToken: String,
    val oauthVerifier: String
)

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

@Serializable
data class LogoutRequest(
    val refreshToken: String
)

@Serializable
sealed interface LogoutResponse {
    @Serializable
    data object Success : LogoutResponse

    @Serializable
    data class Error(
        override val message: String
    ) : LogoutResponse, ContractError.InternalError
}