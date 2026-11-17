plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.serialization)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

group = "amu.wmi.eyelearner"
version = "1.0.0"

dependencies {
    api(project(":core"))
    testImplementation(libs.kotlin.testJunit)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.serialization.json)
}