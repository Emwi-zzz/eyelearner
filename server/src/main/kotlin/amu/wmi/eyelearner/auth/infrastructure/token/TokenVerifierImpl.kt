package amu.wmi.eyelearner.auth.infrastructure.token

import amu.wmi.eyelearner.auth.domain.token.model.AccessTokenRecord
import amu.wmi.eyelearner.auth.domain.token.repository.TokenStorage
import amu.wmi.eyelearner.auth.domain.token.service.TokenVerifier
import org.springframework.stereotype.Service

@Service
class TokenVerifierImpl(
    private val tokenStorage: TokenStorage
) : TokenVerifier {

    override suspend fun verifyAccessToken(token: String): AccessTokenRecord? {
        return tokenStorage.findAccessToken(token)
    }
}
