package amu.wmi.eyelearner.app.auth.data.remote

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

interface AuthRemoteDataSource {
    suspend fun initiateLogin(callbackUrl: String): InitLoginResponse
    suspend fun finalizeLogin(oauthToken: String, oauthVerifier: String): AuthSessionResponse
    suspend fun refreshSession(refreshToken: String): AuthSessionResponse
    suspend fun logout(refreshToken: String): LogoutResponse
}

class AuthRemoteDataSourceImpl(
    private val httpClient: HttpClient
) : AuthRemoteDataSource {

    override suspend fun initiateLogin(callbackUrl: String): InitLoginResponse {
        return try {
            val response = httpClient.post(AuthEndpoints.LOGIN_INIT) {
                setBody(InitLoginRequest(callbackUrl))
            }
            if (response.status.isSuccess()) {
                response.body<InitLoginResponse.Success>()
            } else {
                val errorBody = runCatching { response.body<InitLoginResponse.Error>() }.getOrNull()
                InitLoginResponse.Error(errorBody?.message ?: "Failed to initiate login (${response.status.value})")
            }
        } catch (e: Exception) {
            InitLoginResponse.Error(e.message ?: "Failed to initiate login")
        }
    }

    override suspend fun finalizeLogin(oauthToken: String, oauthVerifier: String): AuthSessionResponse {
        return try {
            val response = httpClient.post(AuthEndpoints.LOGIN_FINALIZE) {
                setBody(FinalizeLoginRequest(oauthToken, oauthVerifier))
            }
            when {
                response.status.isSuccess() -> response.body<AuthSessionResponse.Success>()
                response.status == HttpStatusCode.Unauthorized -> AuthSessionResponse.InvalidOrExpiredToken
                else -> {
                    val errorBody = runCatching { response.body<AuthSessionResponse.Error>() }.getOrNull()
                    AuthSessionResponse.Error(errorBody?.message ?: "Failed to finalize login (${response.status.value})")
                }
            }
        } catch (e: Exception) {
            AuthSessionResponse.Error(e.message ?: "Failed to finalize login")
        }
    }

    override suspend fun refreshSession(refreshToken: String): AuthSessionResponse {
        return try {
            val response = httpClient.post(AuthEndpoints.REFRESH) {
                setBody(RefreshSessionRequest(refreshToken))
            }
            when {
                response.status.isSuccess() -> response.body<AuthSessionResponse.Success>()
                response.status == HttpStatusCode.Unauthorized -> AuthSessionResponse.InvalidOrExpiredToken
                else -> {
                    val errorBody = runCatching { response.body<AuthSessionResponse.Error>() }.getOrNull()
                    AuthSessionResponse.Error(errorBody?.message ?: "Failed to refresh session (${response.status.value})")
                }
            }
        } catch (e: Exception) {
            AuthSessionResponse.Error(e.message ?: "Failed to refresh session")
        }
    }

    override suspend fun logout(refreshToken: String): LogoutResponse {
        return try {
            val response = httpClient.post(AuthEndpoints.LOGOUT) {
                setBody(LogoutRequest(refreshToken))
            }
            if (response.status.isSuccess()) {
                LogoutResponse.Success
            } else {
                val errorBody = runCatching { response.body<LogoutResponse.Error>() }.getOrNull()
                LogoutResponse.Error(errorBody?.message ?: "Failed to logout (${response.status.value})")
            }
        } catch (e: Exception) {
            LogoutResponse.Error(e.message ?: "Failed to logout")
        }
    }
}
