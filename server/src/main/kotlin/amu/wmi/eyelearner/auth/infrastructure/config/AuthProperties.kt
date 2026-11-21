package amu.wmi.eyelearner.auth.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "eyelearner.auth")
data class AuthProperties(
    var accessTokenExpirationSeconds: Long = 900,
    var refreshTokenExpirationSeconds: Long = 604800
)
