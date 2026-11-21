package amu.wmi.eyelearner.common.web

import amu.wmi.eyelearner.common.contract.ContractError
import org.springframework.http.HttpStatus

class ContractErrorException(
    val status: HttpStatus,
    val errorBody: ContractError
) : RuntimeException(errorBody.message)

fun evaluateErrorCode(error: ContractError): ContractErrorException {
    val status = when (error) {
        is ContractError.BadRequest -> HttpStatus.BAD_REQUEST
        is ContractError.Unauthorized -> HttpStatus.UNAUTHORIZED
        is ContractError.Forbidden -> HttpStatus.FORBIDDEN
        is ContractError.NotFound -> HttpStatus.NOT_FOUND
        is ContractError.InternalError -> HttpStatus.INTERNAL_SERVER_ERROR
        is ContractError.BadGateway -> HttpStatus.BAD_GATEWAY
    }
    return ContractErrorException(status, error)
}

fun <T : Any> T.orThrowIfError(): T {
    if (this is ContractError) {
        throw evaluateErrorCode(this)
    }
    return this
}
