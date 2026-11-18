package amu.wmi.eyelearner.domain.token.model

import java.time.Instant

data class AccessTokenRecord(
    val token: String,
    val userId: String,
    val expiresAt: Instant
) {
    fun isExpired(now: Instant = Instant.now()): Boolean = now.isAfter(expiresAt)
}

data class RefreshTokenRecord(
    val token: String,
    val userId: String,
    val associatedAccessToken: String?,
    val expiresAt: Instant
) {
    fun isExpired(now: Instant = Instant.now()): Boolean = now.isAfter(expiresAt)
}
