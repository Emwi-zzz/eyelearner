package amu.wmi.eyelearner

import amu.wmi.eyelearner.config.AuthProperties
import amu.wmi.eyelearner.config.UsosProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(UsosProperties::class, AuthProperties::class)
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}