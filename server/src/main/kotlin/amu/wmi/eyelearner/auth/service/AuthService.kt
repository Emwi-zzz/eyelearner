package amu.wmi.eyelearner.auth.service

import amu.wmi.eyelearner.auth.contract.LoginContract
import amu.wmi.eyelearner.auth.contract.LogoutContract
import amu.wmi.eyelearner.auth.contract.RefreshSessionContract
import org.springframework.stereotype.Service

@Service
class AuthService : LoginContract, LogoutContract, RefreshSessionContract