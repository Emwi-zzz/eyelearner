package amu.wmi.eyelearner.auth.presentation

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.contract.RefreshSessionContract
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import amu.wmi.eyelearner.common.web.orThrowIfError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RefreshSessionController(@Qualifier("authService") private val service: RefreshSessionContract) : RefreshSessionContract {

    @PostMapping(AuthEndpoints.REFRESH)
    override suspend fun refresh(@RequestBody request: RefreshSessionRequest): AuthSessionResponse =
        service.refresh(request).orThrowIfError()
}
