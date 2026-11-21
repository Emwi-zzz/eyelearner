package amu.wmi.eyelearner.common.web

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.reflect.full.createType

@Serializable
private data class FallbackErrorBody(val message: String)

@RestControllerAdvice
class GlobalContractExceptionHandler {

    private val json = Json { ignoreUnknownKeys = true }

    @ExceptionHandler(ContractErrorException::class)
    fun handleContractError(ex: ContractErrorException): ResponseEntity<String> {
        val body = ex.errorBody
        val errorJson = runCatching {
            val serializer = serializer(body::class.createType())
            @Suppress("UNCHECKED_CAST")
            json.encodeToString(serializer as KSerializer<Any>, body)
        }.getOrElse {
            json.encodeToString(FallbackErrorBody.serializer(), FallbackErrorBody(body.message ?: "Unknown error"))
        }

        return ResponseEntity.status(ex.status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorJson)
    }
}
