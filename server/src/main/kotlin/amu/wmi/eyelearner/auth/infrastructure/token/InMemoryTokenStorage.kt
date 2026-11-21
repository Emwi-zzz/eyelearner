package amu.wmi.eyelearner.auth.infrastructure.token

import amu.wmi.eyelearner.auth.domain.token.model.AccessTokenRecord
import amu.wmi.eyelearner.auth.domain.token.model.RefreshTokenRecord
import amu.wmi.eyelearner.auth.domain.token.repository.TokenStorage
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryTokenStorage : TokenStorage {

    private val accessTokens = ConcurrentHashMap<String, AccessTokenRecord>()
    private val refreshTokens = ConcurrentHashMap<String, RefreshTokenRecord>()

    override suspend fun storeAccessToken(token: String, userId: String, ttl: Duration) {
        val expiresAt = Instant.now().plus(ttl)
        accessTokens[token] = AccessTokenRecord(token, userId, expiresAt)
    }

    override suspend fun findAccessToken(token: String): AccessTokenRecord? {
        val record = accessTokens[token] ?: return null
        if (record.isExpired()) {
            accessTokens.remove(token, record)
            return null
        }
        return record
    }

    override suspend fun revokeAccessToken(token: String) {
        accessTokens.remove(token)
    }

    override suspend fun storeRefreshToken(
        token: String,
        userId: String,
        associatedAccessToken: String?,
        ttl: Duration
    ) {
        val expiresAt = Instant.now().plus(ttl)
        refreshTokens[token] = RefreshTokenRecord(token, userId, associatedAccessToken, expiresAt)
    }

    override suspend fun findRefreshToken(token: String): RefreshTokenRecord? {
        val record = refreshTokens[token] ?: return null
        if (record.isExpired()) {
            refreshTokens.remove(token, record)
            return null
        }
        return record
    }

    override suspend fun revokeRefreshToken(token: String) {
        refreshTokens.remove(token)
    }

    fun clear() {
        accessTokens.clear()
        refreshTokens.clear()
    }
}
