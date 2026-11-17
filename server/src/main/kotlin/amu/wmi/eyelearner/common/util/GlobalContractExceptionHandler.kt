package amu.wmi.eyelearner.common.util

import amu.wmi.eyelearner.common.contract.ContractError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

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

@RestControllerAdvice
class GlobalContractExceptionHandler {
    @ExceptionHandler(ContractErrorException::class)
    fun handleContractError(ex: ContractErrorException): ResponseEntity<ContractError> {
        return ResponseEntity.status(ex.status).body(ex.errorBody)
    }
}