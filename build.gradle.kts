@file:Suppress("DEPRECATION")

val ktorVersion = "2.2.2"
val kotlinVersion = "1.7.10"
val logbackVersion = "1.4.4"

plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
}

group = "ru.kheynov"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

ktlint {
    ignoreFailures.set(false)
    disabledRules.set(setOf("no-wildcard-imports"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.testng:testng:7.7.0")
    implementation("io.ktor:ktor-server-websockets-jvm:2.2.2")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-websockets:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    implementation("io.ktor:ktor-server-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    implementation("io.swagger.codegen.v3:swagger-codegen-generators:1.0.36")

    implementation("at.favre.lib:bcrypt:0.9.0")

    implementation("io.insert-koin:koin-ktor:3.3.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.3.0")
    // Firebase admin
    implementation("org.slf4j:slf4j-simple:2.0.5")

    // Database
    implementation("org.ktorm:ktorm-core:3.5.0")
    implementation("org.ktorm:ktorm-support-postgresql:3.5.0")
    implementation("org.postgresql:postgresql:42.5.1")
    testImplementation("io.ktor:ktor-server-test-host-jvm:2.2.2")

    // metrics
    implementation("io.ktor:ktor-metrics:1.6.8")
    implementation("io.ktor:ktor-metrics-micrometer:1.6.8")
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:2.2.2")
    implementation("io.micrometer:micrometer-registry-prometheus:1.10.3")

    // HTTP client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
}