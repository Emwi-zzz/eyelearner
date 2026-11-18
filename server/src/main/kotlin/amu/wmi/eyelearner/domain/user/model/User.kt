package amu.wmi.eyelearner.domain.user.model

import java.time.Instant

data class User(
    val id: String,
    val usosId: String,
    val firstName: String,
    val lastName: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
