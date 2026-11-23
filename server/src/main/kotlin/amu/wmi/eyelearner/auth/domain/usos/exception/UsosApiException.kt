package amu.wmi.eyelearner.auth.domain.usos.exception

class UsosApiException(
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)
