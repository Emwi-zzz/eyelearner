package amu.wmi.eyelearner

import amu.wmi.eyelearner.auth.infrastructure.config.AuthProperties
import amu.wmi.eyelearner.auth.infrastructure.config.UsosProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(UsosProperties::class, AuthProperties::class)
class ServerApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<ServerApplication>(*args)
        }
    }
}

fun main(args: Array<String>) {
    ServerApplication.main(args)
}