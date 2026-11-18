package amu.wmi.eyelearner.infrastructure.usos

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

class UsosApiException(
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

interface UsosOAuthClient {
    suspend fun getRequestToken(callbackUrl: String, scopes: String? = null): UsosRequestToken
    suspend fun getAccessToken(oauthToken: String, oauthVerifier: String, requestTokenSecret: String): UsosAccessToken
    suspend fun getUserProfile(accessToken: String, accessTokenSecret: String): UsosUserProfile
}
