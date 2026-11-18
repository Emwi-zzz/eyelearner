package amu.wmi.eyelearner.infrastructure.persistence

import amu.wmi.eyelearner.domain.token.model.AccessTokenRecord
import amu.wmi.eyelearner.domain.token.model.RefreshTokenRecord
import amu.wmi.eyelearner.domain.token.repository.TokenStorage
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryTokenStorage : TokenStorage {

    private val accessTokens = ConcurrentHashMap<String, AccessTokenRecord>()
    private val refreshTokens = ConcurrentHashMap<String, RefreshTokenRecord>()
    private val requestTokenSecrets = ConcurrentHashMap<String, RequestTokenEntry>()

    private data class RequestTokenEntry(
        val tokenSecret: String,
        val expiresAt: Instant
    )

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

    override suspend fun storeRequestTokenSecret(oauthToken: String, tokenSecret: String, ttl: Duration) {
        val expiresAt = Instant.now().plus(ttl)
        requestTokenSecrets[oauthToken] = RequestTokenEntry(tokenSecret, expiresAt)
    }

    override suspend fun getAndRemoveRequestTokenSecret(oauthToken: String): String? {
        val entry = requestTokenSecrets.remove(oauthToken) ?: return null
        if (Instant.now().isAfter(entry.expiresAt)) {
            return null
        }
        return entry.tokenSecret
    }

    fun clear() {
        accessTokens.clear()
        refreshTokens.clear()
        requestTokenSecrets.clear()
    }
}
