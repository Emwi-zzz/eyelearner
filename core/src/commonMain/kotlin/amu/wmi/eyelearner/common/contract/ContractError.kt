package amu.wmi.eyelearner.common.contract

sealed interface ContractError {
    val message: String? get() = null

    interface BadRequest : ContractError       // 400
    interface Unauthorized : ContractError     // 401
    interface Forbidden : ContractError        // 403
    interface NotFound : ContractError         // 404
    interface InternalError : ContractError    // 500
    interface BadGateway : ContractError       // 502 (e.g., USOS is down)
}