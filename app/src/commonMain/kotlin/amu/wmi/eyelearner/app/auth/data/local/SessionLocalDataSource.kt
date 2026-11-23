package amu.wmi.eyelearner.app.auth.data.local

import amu.wmi.eyelearner.app.auth.domain.entities.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface SessionLocalDataSource {
    val sessionFlow: Flow<Session?>
    suspend fun getSession(): Session?
    suspend fun saveSession(session: Session)
    suspend fun clearSession()
}

class SessionLocalDataSourceImpl : SessionLocalDataSource {
    private val _session = MutableStateFlow<Session?>(null)
    override val sessionFlow: Flow<Session?> = _session.asStateFlow()

    override suspend fun getSession(): Session? {
        return _session.value
    }

    override suspend fun saveSession(session: Session) {
        _session.value = session
    }

    override suspend fun clearSession() {
        _session.value = null
    }
}
