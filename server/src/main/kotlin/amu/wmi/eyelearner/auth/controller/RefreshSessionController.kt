package amu.wmi.eyelearner.auth.controller

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.contract.RefreshSessionContract
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import amu.wmi.eyelearner.common.contract.ContractError
import amu.wmi.eyelearner.common.util.evaluateErrorCode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RefreshSessionController(@Qualifier("authService") val service: RefreshSessionContract) : RefreshSessionContract {

    @PostMapping(AuthEndpoints.REFRESH)
    override suspend fun refresh(@RequestBody request: RefreshSessionRequest): AuthSessionResponse {
        return when (val response = service.refresh(request)) {
            is AuthSessionResponse.Success -> response
            is ContractError -> throw evaluateErrorCode(response)
        }
    }
}