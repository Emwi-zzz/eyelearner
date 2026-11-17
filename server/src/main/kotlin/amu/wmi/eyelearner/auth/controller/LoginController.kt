package amu.wmi.eyelearner.auth.controller

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.contract.LoginContract
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse
import amu.wmi.eyelearner.common.contract.ContractError
import amu.wmi.eyelearner.common.util.evaluateErrorCode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class LoginController(val service: LoginContract) : LoginContract {

    @PostMapping(AuthEndpoints.LOGIN_INIT)
    override suspend fun initiate(@RequestBody request: InitLoginRequest): InitLoginResponse {
        return when (val response = service.initiate(request)) {
            is InitLoginResponse.Success -> response
            is ContractError -> throw evaluateErrorCode(response)
        }
    }

    @PostMapping(AuthEndpoints.LOGIN_FINALIZE)
    override suspend fun finalize(@RequestBody request: FinalizeLoginRequest): AuthSessionResponse {
        return when (val response = service.finalize(request)) {
            is AuthSessionResponse.Success -> response
            is ContractError -> throw evaluateErrorCode(response)
        }
    }
}