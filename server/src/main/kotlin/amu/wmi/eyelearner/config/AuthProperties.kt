package amu.wmi.eyelearner.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "eyelearner.usos")
data class UsosProperties(
    var baseUrl: String = "https://usosapps.amu.edu.pl",
    var consumerKey: String = "test-consumer-key",
    var consumerSecret: String = "test-consumer-secret",
    var scopes: String = ""
)

@ConfigurationProperties(prefix = "eyelearner.auth")
data class AuthProperties(
    var accessTokenExpirationSeconds: Long = 900,
    var refreshTokenExpirationSeconds: Long = 604800
)
