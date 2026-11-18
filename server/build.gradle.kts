plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.serialization)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

group = "amu.wmi.eyelearner"
version = "1.0.0"

dependencies {
    api(project(":core"))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.serialization.json)
    implementation(kotlin("reflect"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation(libs.kotlin.testJunit)
}

tasks.withType<Test> {
    useJUnitPlatform()
}