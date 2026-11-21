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

extra["kotlin-coroutines.version"] = libs.versions.kotlinx.coroutines.get()

dependencies {
    api(project(":core"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:${libs.versions.kotlinx.coroutines.get()}"))
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.serialization.json)
    implementation(kotlin("reflect"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation(libs.kotlin.testJunit)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("run") {
    dependsOn("bootRun")
}