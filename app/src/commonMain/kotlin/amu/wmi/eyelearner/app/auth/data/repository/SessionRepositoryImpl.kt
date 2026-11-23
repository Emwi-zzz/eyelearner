package amu.wmi.eyelearner.app.auth.data.repository

import amu.wmi.eyelearner.app.auth.data.local.SessionLocalDataSource
import amu.wmi.eyelearner.app.auth.data.remote.AuthRemoteDataSource
import amu.wmi.eyelearner.app.auth.domain.entities.Session
import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import kotlinx.coroutines.flow.Flow

class SessionRepositoryImpl(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionLocalDataSource: SessionLocalDataSource
) : SessionRepository {

    override suspend fun getSession(): Session? {
        return sessionLocalDataSource.getSession()
    }

    override suspend fun saveSession(session: Session) {
        sessionLocalDataSource.saveSession(session)
    }

    override suspend fun clearSession() {
        sessionLocalDataSource.clearSession()
    }

    override suspend fun initSession(callBackUrl: String): InitLoginResponse {
        return authRemoteDataSource.initiateLogin(callBackUrl)
    }

    override suspend fun finalizeSession(oauthToken: String, oauthVerifier: String): AuthSessionResponse {
        val response = authRemoteDataSource.finalizeLogin(oauthToken, oauthVerifier)
        if (response is AuthSessionResponse.Success) {
            val session = Session(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = response.userId,
                expiresInSeconds = response.expiresInSeconds
            )
            sessionLocalDataSource.saveSession(session)
        }
        return response
    }

    override suspend fun refreshSession(): AuthSessionResponse {
        val currentSession = sessionLocalDataSource.getSession()
            ?: return AuthSessionResponse.InvalidOrExpiredToken

        val response = authRemoteDataSource.refreshSession(currentSession.refreshToken)
        if (response is AuthSessionResponse.Success) {
            val updated = currentSession.copy(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                userId = if (response.userId.isNotBlank()) response.userId else currentSession.userId,
                expiresInSeconds = response.expiresInSeconds
            )
            sessionLocalDataSource.saveSession(updated)
        } else if (response is AuthSessionResponse.InvalidOrExpiredToken) {
            sessionLocalDataSource.clearSession()
        }
        return response
    }

    override suspend fun logout(): LogoutResponse {
        val currentSession = sessionLocalDataSource.getSession()
        val response = if (currentSession != null) {
            authRemoteDataSource.logout(currentSession.refreshToken)
        } else {
            LogoutResponse.Success
        }
        sessionLocalDataSource.clearSession()
        return response
    }

    override fun observeSession(): Flow<Session?> {
        return sessionLocalDataSource.sessionFlow
    }
}
