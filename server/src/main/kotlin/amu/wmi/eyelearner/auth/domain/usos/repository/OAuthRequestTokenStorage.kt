package amu.wmi.eyelearner.auth.domain.usos.repository

import java.time.Duration

interface OAuthRequestTokenStorage {
    suspend fun storeRequestTokenSecret(oauthToken: String, tokenSecret: String, ttl: Duration)
    suspend fun getAndRemoveRequestTokenSecret(oauthToken: String): String?
}
