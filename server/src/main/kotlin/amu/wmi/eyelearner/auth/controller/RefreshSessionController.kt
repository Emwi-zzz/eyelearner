package amu.wmi.eyelearner.auth.controller

import amu.wmi.eyelearner.auth.AuthEndpoints
import amu.wmi.eyelearner.auth.contract.RefreshSessionContract
import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RefreshSessionController : RefreshSessionContract {
    @PostMapping(AuthEndpoints.REFRESH)
    override suspend fun refresh(@RequestBody request: RefreshSessionRequest): AuthSessionResponse {
        TODO("Not yet implemented")
    }
}