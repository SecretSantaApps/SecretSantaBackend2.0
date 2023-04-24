package ru.kheynov.santa.api.v1.routing

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.kheynov.santa.domain.repositories.GameRepository
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.utils.UpdateModel

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    encodeDefaults = true
    explicitNulls = false
}

fun Route.webSockets(
    usersRepository: UsersRepository,
    roomsRepository: RoomsRepository,
    gameRepository: GameRepository,
) {
    authenticate {
        webSocket {
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                send(Frame.Text("No access token provided"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No access token provided"))
                return@webSocket
            }
            val roomId = call.request.queryParameters["id"] ?: run {
                send(Frame.Text("Wrong room id"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Wrong room id"))
                return@webSocket
            }
            if (usersRepository.getUserByID(userId) == null) {
                send(Frame.Text("User not exists"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not exists"))
                return@webSocket
            }

            if (!gameRepository.checkUserInRoom(roomId = roomId, userId = userId)) {
                send(Frame.Text("User not in the room"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not in the room"))
                return@webSocket
            }

            val roomUpdateHandler = async {
                roomsRepository.updates
                    .collect { update ->
                        if (update !is UpdateModel.RoomUpdate) return@collect
                        if (update.roomId != roomId) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }

            val gameUpdateHandler = async {
                gameRepository.updates
                    .collect { update ->
                        if (update is UpdateModel.GameStateUpdate && update.roomId != roomId) return@collect
                        if (update is UpdateModel.UsersUpdate && update.roomId != roomId) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }
            awaitAll(roomUpdateHandler, gameUpdateHandler)
        }
    }

    authenticate {
        webSocket("/all") { // subscribe on all user's rooms updates
            val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                send(Frame.Text("No access token provided"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No access token provided"))
                return@webSocket
            }
            if (usersRepository.getUserByID(userId) == null) {
                send(Frame.Text("User not exists"))
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "User not exists"))
                return@webSocket
            }
            val userRooms = roomsRepository.getUserRooms(userId)

            val roomUpdateHandler = async {
                roomsRepository.updates
                    .collect { update ->
                        if (update !is UpdateModel.RoomUpdate) return@collect
                        if (userRooms.find { it.id == update.roomId } == null) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }

            val gameUpdateHandler = async {
                gameRepository.updates
                    .collect { update ->
                        if (update is UpdateModel.GameStateUpdate && userRooms.find { it.id == update.roomId } == null) return@collect
                        if (update is UpdateModel.UsersUpdate && userRooms.find { it.id == update.roomId } == null) return@collect
                        send(Frame.Text(json.encodeToString(update)))
                    }
            }
            awaitAll(roomUpdateHandler, gameUpdateHandler)
        }
    }
}