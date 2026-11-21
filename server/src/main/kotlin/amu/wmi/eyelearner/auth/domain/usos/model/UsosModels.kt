package amu.wmi.eyelearner.auth.domain.usos.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class UsosRequestToken(
    val oauthToken: String,
    val oauthTokenSecret: String,
    val callbackConfirmed: Boolean
)

data class UsosAccessToken(
    val oauthToken: String,
    val oauthTokenSecret: String
)

@Serializable
data class UsosUserProfile(
    val id: String,
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = ""
)
