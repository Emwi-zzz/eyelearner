package amu.wmi.eyelearner.auth.controller

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.contract.LogoutContract
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.common.contract.ContractError
import amu.wmi.eyelearner.common.util.evaluateErrorCode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LogoutController(@Qualifier("authService") val service: LogoutContract) : LogoutContract {

    @PostMapping(AuthEndpoints.LOGOUT)
    override suspend fun logout(@RequestBody request: LogoutRequest): LogoutResponse {
        return when (val response = service.logout(request)) {
            is LogoutResponse.Success -> response
            is ContractError -> throw evaluateErrorCode(response)
        }
    }
}