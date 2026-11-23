package amu.wmi.eyelearner.app.auth.domain.usecase

import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository
import amu.wmi.eyelearner.auth.dto.LogoutResponse

class LogoutUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(): LogoutResponse {
        return sessionRepository.logout()
    }
}
