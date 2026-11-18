package amu.wmi.eyelearner.infrastructure.security

import amu.wmi.eyelearner.domain.token.model.AccessTokenRecord
import amu.wmi.eyelearner.domain.token.repository.TokenStorage
import amu.wmi.eyelearner.domain.token.service.TokenVerifier
import org.springframework.stereotype.Service

@Service
class TokenVerifierImpl(
    private val tokenStorage: TokenStorage
) : TokenVerifier {

    override suspend fun verifyAccessToken(token: String): AccessTokenRecord? {
        return tokenStorage.findAccessToken(token)
    }
}
