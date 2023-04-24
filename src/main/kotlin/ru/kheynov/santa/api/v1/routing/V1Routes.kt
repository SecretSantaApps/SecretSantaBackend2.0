package ru.kheynov.santa.api.v1.routing

import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.kheynov.santa.domain.repositories.GameRepository
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.domain.useCases.UseCases

fun Route.v1Routes() {
    route("/v1") {
        val useCases by inject<UseCases>()
        val roomsRepository by inject<RoomsRepository>()
        val gameRepository by inject<GameRepository>()
        val usersRepository by inject<UsersRepository>()

        configureUserRoutes(useCases)
        configureRoomsRoutes(useCases)
        configureGameRoutes(
            useCases = useCases,
            usersRepository = usersRepository,
            roomsRepository = roomsRepository,
            gameRepository = gameRepository,
        )
    }
}