package amu.wmi.eyelearner.app.auth.domain.usecase

import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse

class FinalizeLoginUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(oauthToken: String, oauthVerifier: String): AuthSessionResponse {
        return sessionRepository.finalizeSession(oauthToken, oauthVerifier)
    }
}
