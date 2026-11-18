package amu.wmi.eyelearner.domain.token.service

import amu.wmi.eyelearner.domain.token.model.AccessTokenRecord

interface TokenVerifier {
    suspend fun verifyAccessToken(token: String): AccessTokenRecord?
}
