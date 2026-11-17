package amu.wmi.eyelearner.auth

object AuthEndpoints {
    const val BASE = "/api/v1/auth"
    const val LOGIN_INIT = "$BASE/login/init"
    const val LOGIN_FINALIZE = "$BASE/login/finalize"
    const val REFRESH = "$BASE/refresh"
    const val LOGOUT = "$BASE/logout"
}