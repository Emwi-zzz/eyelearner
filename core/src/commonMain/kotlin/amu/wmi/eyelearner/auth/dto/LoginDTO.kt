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
