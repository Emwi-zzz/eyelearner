package amu.wmi.eyelearner.common.util

import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.common.contract.ContractError
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

    private val json = Json { ignoreUnknownKeys = true }

    @ExceptionHandler(ContractErrorException::class)
    fun handleContractError(ex: ContractErrorException): ResponseEntity<String> {
        val errorJson = when (val body = ex.errorBody) {
            is InitLoginResponse.Error -> json.encodeToString(InitLoginResponse.Error.serializer(), body)
            is AuthSessionResponse.InvalidOrExpiredToken -> json.encodeToString(AuthSessionResponse.InvalidOrExpiredToken.serializer(), body)
            is AuthSessionResponse.Error -> json.encodeToString(AuthSessionResponse.Error.serializer(), body)
            is LogoutResponse.Error -> json.encodeToString(LogoutResponse.Error.serializer(), body)
            else -> """{"message":"${body.message ?: "Unknown error"}"}"""
        }
        return ResponseEntity.status(ex.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorJson)
    }
}