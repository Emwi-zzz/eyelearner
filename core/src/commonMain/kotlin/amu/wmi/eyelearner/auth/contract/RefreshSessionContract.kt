package amu.wmi.eyelearner.auth.contract

import amu.wmi.eyelearner.auth.dto.AuthSessionResponse
import amu.wmi.eyelearner.auth.dto.RefreshSessionRequest

interface RefreshSessionContract {
    suspend fun refresh(request: RefreshSessionRequest): AuthSessionResponse
}