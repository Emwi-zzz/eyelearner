package amu.wmi.eyelearner.auth.contract

import amu.wmi.eyelearner.auth.dto.LogoutRequest
import amu.wmi.eyelearner.auth.dto.LogoutResponse

interface LogoutContract {
    suspend fun logout(request: LogoutRequest): LogoutResponse
}