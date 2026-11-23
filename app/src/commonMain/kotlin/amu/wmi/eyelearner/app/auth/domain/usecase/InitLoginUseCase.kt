package amu.wmi.eyelearner.app.auth.domain.usecase

import amu.wmi.eyelearner.app.auth.domain.repository.SessionRepository
import amu.wmi.eyelearner.auth.dto.InitLoginResponse

class InitLoginUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke(callbackUrl: String = ""): String {
        return when (val response = sessionRepository.initSession(callbackUrl)) {
            is InitLoginResponse.Success -> response.authorizeUrl
            is InitLoginResponse.Error -> error(response.message)
        }
    }
}