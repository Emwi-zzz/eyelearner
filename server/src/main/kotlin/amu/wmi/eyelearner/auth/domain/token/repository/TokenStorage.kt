package amu.wmi.eyelearner.auth.domain.token.repository

import amu.wmi.eyelearner.auth.domain.token.model.AccessTokenRecord
import amu.wmi.eyelearner.auth.domain.token.model.RefreshTokenRecord
import java.time.Duration

interface TokenStorage {
    suspend fun storeAccessToken(token: String, userId: String, ttl: Duration)
    suspend fun findAccessToken(token: String): AccessTokenRecord?
    suspend fun revokeAccessToken(token: String)

    suspend fun storeRefreshToken(token: String, userId: String, associatedAccessToken: String?, ttl: Duration)
    suspend fun findRefreshToken(token: String): RefreshTokenRecord?
    suspend fun revokeRefreshToken(token: String)
}
