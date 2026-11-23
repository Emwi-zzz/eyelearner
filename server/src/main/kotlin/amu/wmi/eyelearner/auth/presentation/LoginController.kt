package amu.wmi.eyelearner.auth.presentation

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.contract.LoginContract
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.common.web.orThrowIfError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(@Qualifier("authService") private val service: LoginContract) : LoginContract {

    @PostMapping(AuthEndpoints.LOGIN_INIT)
    override suspend fun initiate(@RequestBody request: InitLoginRequest): InitLoginResponse =
        service.initiate(request).orThrowIfError()

    @PostMapping(AuthEndpoints.LOGIN_FINALIZE)
    override suspend fun finalize(@RequestBody request: FinalizeLoginRequest): AuthSessionResponse =
        service.finalize(request).orThrowIfError()
}
