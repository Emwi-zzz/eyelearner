package amu.wmi.eyelearner.app.auth.domain.usecase

import amu.wmi.eyelearner.app.auth.domain.entities.Session
import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository

class GetSessionUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(): Session? {
        return sessionRepository.getSession()
    }
}
