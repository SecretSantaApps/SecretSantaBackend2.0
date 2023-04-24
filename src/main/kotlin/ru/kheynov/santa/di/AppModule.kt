package ru.kheynov.santa.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.ktorm.database.Database
import ru.kheynov.santa.data.repositories.game.PostgresGameRepository
import ru.kheynov.santa.data.repositories.rooms.PostgresRoomsRepository
import ru.kheynov.santa.data.repositories.users.PostgresUsersRepository
import ru.kheynov.santa.domain.repositories.GameRepository
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.domain.services.OneSignalService
import ru.kheynov.santa.domain.useCases.UseCases
import ru.kheynov.santa.security.jwt.hashing.BcryptHashingService
import ru.kheynov.santa.security.jwt.hashing.HashingService
import ru.kheynov.santa.security.jwt.token.JwtTokenService
import ru.kheynov.santa.security.jwt.token.TokenConfig
import ru.kheynov.santa.security.jwt.token.TokenService
import ru.kheynov.santa.services.OneSignalServiceImpl
import ru.kheynov.santa.utils.GiftDispenser
import ru.kheynov.santa.utils.SimpleCycleGiftDispenser

@OptIn(ExperimentalSerializationApi::class)
val appModule = module {
    single {
        Database.connect(
            url = System.getenv("DATABASE_CONNECTION_STRING"),
            driver = "org.postgresql.Driver",
            user = System.getenv("POSTGRES_NAME"),
            password = System.getenv("POSTGRES_PASSWORD"),
        )
    }

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = false
                        explicitNulls = false
                    },
                )
            }
        }
    }

    single<OneSignalService> { OneSignalServiceImpl(get()) }

    single<UsersRepository> { PostgresUsersRepository(get()) }
    single<RoomsRepository> { PostgresRoomsRepository(get()) }
    single<GameRepository> { PostgresGameRepository(get()) }

    single {
        TokenConfig(
            issuer = System.getenv("JWT_ISSUER"),
            audience = System.getenv("JWT_AUDIENCE"),
            accessLifetime = System.getenv("JWT_ACCESS_LIFETIME").toLong(),
            refreshLifetime = System.getenv("JWT_REFRESH_LIFETIME").toLong(),
            secret = System.getenv("JWT_SECRET"),
        )
    }

    single<TokenService> { JwtTokenService() }

    single<HashingService> { BcryptHashingService() }

    single<GiftDispenser> { SimpleCycleGiftDispenser() }

    single { UseCases() }

    single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
}