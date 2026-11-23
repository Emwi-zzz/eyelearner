package amu.wmi.eyelearner.auth.application

import amu.wmi.eyelearner.auth.application.usecase.FinalizeLoginUseCase
import amu.wmi.eyelearner.auth.application.usecase.InitiateLoginUseCase
import amu.wmi.eyelearner.auth.application.usecase.LogoutUseCase
import amu.wmi.eyelearner.auth.application.usecase.RefreshSessionUseCase
import amu.wmi.eyelearner.auth.contract.LoginContract
import amu.wmi.eyelearner.auth.contract.LogoutContract
import amu.wmi.eyelearner.auth.contract.RefreshSessionContract
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val initiateLoginUseCase: InitiateLoginUseCase,
    private val finalizeLoginUseCase: FinalizeLoginUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : LoginContract, LogoutContract, RefreshSessionContract {

    override suspend fun initiate(request: InitLoginRequest): InitLoginResponse =
        initiateLoginUseCase.execute(request)

    override suspend fun finalize(request: FinalizeLoginRequest): AuthSessionResponse =
        finalizeLoginUseCase.execute(request)

    override suspend fun refresh(request: RefreshSessionRequest): AuthSessionResponse =
        refreshSessionUseCase.execute(request)

    override suspend fun logout(request: LogoutRequest): LogoutResponse =
        logoutUseCase.execute(request)
}
