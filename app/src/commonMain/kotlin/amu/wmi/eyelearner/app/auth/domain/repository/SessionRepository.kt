package amu.wmi.eyelearner.app.auth.domain.repository

import amu.wmi.eyelearner.app.auth.domain.entities.Session
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun getSession(): Session?

    suspend fun saveSession(session: Session)

    suspend fun clearSession()

    suspend fun initSession(callBackUrl: String): InitLoginResponse

    suspend fun finalizeSession(oauthToken: String, oauthVerifier: String): AuthSessionResponse

    suspend fun refreshSession(): AuthSessionResponse

    suspend fun logout(): LogoutResponse

    fun observeSession(): Flow<Session?>
}