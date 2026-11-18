package amu.wmi.eyelearner.infrastructure.security

import amu.wmi.eyelearner.domain.token.service.TokenGenerator
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
class SecureTokenGenerator : TokenGenerator {

    private val secureRandom = SecureRandom()
    private val base64Encoder = Base64.getUrlEncoder().withoutPadding()

    override fun generateToken(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return base64Encoder.encodeToString(bytes)
    }
}
