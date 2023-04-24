package ru.kheynov.santa

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level
import ru.kheynov.santa.api.v1.routing.v1Routes
import ru.kheynov.santa.di.appModule
import ru.kheynov.santa.security.jwt.token.TokenConfig
import ru.kheynov.santa.services.NotificationService
import java.io.File
import java.time.Duration

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureDI()
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureWebSockets()
    configureRouting()
    configureMicrometrics()
    configureNotificationService()
}

private fun configureNotificationService() {
    NotificationService()
}

private fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

private fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}

private fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond("Secret Santa Server")
        }

        static("/assets") {
            staticRootFolder = File("files")
            files(".")
        }
        route("/api") {
            v1Routes()
        }
        metrics()
    }
}

private fun Application.configureMicrometrics() {
    install(MicrometerMetrics) {
        registry = this@configureMicrometrics.get<PrometheusMeterRegistry>()
        meterBinders = listOf(
            ClassLoaderMetrics(),
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            ProcessorMetrics(),
            JvmThreadMetrics(),
            FileDescriptorMetrics(),
            UptimeMetrics(),
        )
    }
}

private fun Application.configureHTTP() {
    install(CORS) {
        HttpMethod.DefaultMethods.forEach(::allowMethod)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("client-id")
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
    routing {
        openAPI(path = "/openapi", swaggerFile = "openapi/documentation.yaml")
        swaggerUI(path = "/swagger", swaggerFile = "openapi/documentation.yaml")
    }
}

private fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}

private fun Application.configureSecurity() {
    val config: TokenConfig by inject()
    install(Authentication)
    authentication {
        jwt {
            verifier(
                JWT.require(Algorithm.HMAC256(config.secret)).withAudience(config.audience).withIssuer(config.issuer)
                    .build(),
            )
            validate { token ->
                if (token.payload.audience.contains(config.audience) && token.payload.expiresAt.time > System.currentTimeMillis()) {
                    JWTPrincipal(token.payload)
                } else {
                    null
                }
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                encodeDefaults = true
                explicitNulls = false
            },
        )
    }
}

private fun Routing.metrics() {
    val registry = get<PrometheusMeterRegistry>()
    get("/metrics") {
        call.respondText {
            registry.scrape()
        }
    }
}