package amu.wmi.eyelearner.auth.contract

import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.FinalizeLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginRequest
import amu.wmi.eyelearner.auth.dto.InitLoginResponse

interface LoginContract {
    suspend fun initiate(request: InitLoginRequest): InitLoginResponse
    suspend fun finalize(request: FinalizeLoginRequest): AuthSessionResponse
}