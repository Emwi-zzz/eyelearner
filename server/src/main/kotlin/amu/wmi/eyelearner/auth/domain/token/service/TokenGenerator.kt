package amu.wmi.eyelearner.auth.domain.token.service

interface TokenGenerator {
    fun generateToken(): String
}
