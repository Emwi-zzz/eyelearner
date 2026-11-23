package amu.wmi.eyelearner.app.auth.domain.entities

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val accessToken: String,
    val refreshToken: String,
    val userId: String = "",
    val expiresInSeconds: Long = 0
)