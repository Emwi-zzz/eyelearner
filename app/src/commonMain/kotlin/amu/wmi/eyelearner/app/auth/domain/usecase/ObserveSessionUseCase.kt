package amu.wmi.eyelearner.app.auth.domain.usecase

import amu.wmi.eyelearner.app.auth.domain.entities.Session
import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class ObserveSessionUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Flow<Session?> {
        return sessionRepository.observeSession()
    }
}
