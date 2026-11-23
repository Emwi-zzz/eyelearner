package amu.wmi.eyelearner.user.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val firstName: String,
    val lastName: String
)
