package amu.wmi.eyelearner.app.auth.domain.usecase

import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse

class RefreshSessionUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(): AuthSessionResponse {
        return sessionRepository.refreshSession()
    }
}
