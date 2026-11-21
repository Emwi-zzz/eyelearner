package amu.wmi.eyelearner.auth.infrastructure.usos

import amu.wmi.eyelearner.auth.domain.usos.repository.OAuthRequestTokenStorage
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryOAuthRequestTokenStorage : OAuthRequestTokenStorage {

    private val requestTokenSecrets = ConcurrentHashMap<String, RequestTokenEntry>()

    private data class RequestTokenEntry(
        val tokenSecret: String,
        val expiresAt: Instant
    )

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
        requestTokenSecrets.clear()
    }
}
