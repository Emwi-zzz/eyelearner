package amu.wmi.eyelearner.auth.presentation

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.contract.LogoutContract
import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse
import amu.wmi.eyelearner.common.web.orThrowIfError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LogoutController(@Qualifier("authService") private val service: LogoutContract) : LogoutContract {

    @PostMapping(AuthEndpoints.LOGOUT)
    override suspend fun logout(@RequestBody request: LogoutRequest): LogoutResponse =
        service.logout(request).orThrowIfError()
}
