package amu.wmi.eyelearner.domain.token.service

interface TokenGenerator {
    fun generateToken(): String
}
