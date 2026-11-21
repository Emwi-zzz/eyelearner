package amu.wmi.eyelearner.auth.domain.token.service

import amu.wmi.eyelearner.auth.domain.token.model.AccessTokenRecord

interface TokenVerifier {
    suspend fun verifyAccessToken(token: String): AccessTokenRecord?
}
