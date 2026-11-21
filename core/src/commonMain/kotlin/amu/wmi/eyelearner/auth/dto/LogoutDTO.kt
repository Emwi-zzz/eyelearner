package amu.wmi.eyelearner.auth.dto

import amu.wmi.eyelearner.common.contract.ContractError
import kotlinx.serialization.Serializable

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
