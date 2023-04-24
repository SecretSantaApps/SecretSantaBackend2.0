package ru.kheynov.santa.api.v1.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.kheynov.santa.api.v1.requests.game.AcceptUserRequest
import ru.kheynov.santa.api.v1.requests.game.JoinRoomRequest
import ru.kheynov.santa.api.v1.requests.game.KickUserRequest
import ru.kheynov.santa.domain.repositories.GameRepository
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.domain.useCases.UseCases
import ru.kheynov.santa.domain.useCases.game.*

fun Route.configureGameRoutes(
    useCases: UseCases,
    usersRepository: UsersRepository,
    roomsRepository: RoomsRepository,
    gameRepository: GameRepository,
) {
    route("/game") {
        webSockets(usersRepository, roomsRepository, gameRepository)

        authenticate {
            post("/join") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val request = call.receiveNullable<JoinRoomRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val res = useCases.joinRoomUseCase(
                    userId = userId,
                    roomId = request.roomId,
                    wishlist = request.wishlist,
                )
                when (res) {
                    JoinRoomUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    JoinRoomUseCase.Result.GameAlreadyStarted -> {
                        call.respond(HttpStatusCode.Conflict, "Game already started")
                        return@post
                    }

                    JoinRoomUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    JoinRoomUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }

                    JoinRoomUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    JoinRoomUseCase.Result.UserAlreadyInRoom -> {
                        call.respond(HttpStatusCode.Conflict, "User already in room")
                        return@post
                    }
                }
            }
        }
        authenticate {
            post("/leave") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val id = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@post
                }

                val res = useCases.leaveRoomUseCase(
                    userId = userId,
                    roomId = id,
                )
                when (res) {
                    LeaveRoomUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    LeaveRoomUseCase.Result.GameAlreadyStarted -> {
                        call.respond(HttpStatusCode.Conflict, "Game already started")
                        return@post
                    }

                    LeaveRoomUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    LeaveRoomUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }

                    LeaveRoomUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    LeaveRoomUseCase.Result.UserNotInRoom -> {
                        call.respond(HttpStatusCode.Forbidden, "User not in the room")
                        return@post
                    }
                }
            }
        }
        authenticate {
            post("/kick") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val request = call.receiveNullable<KickUserRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val res = useCases.kickUserUseCase(
                    selfId = userId,
                    userId = request.userId,
                    roomId = request.roomId,
                )
                when (res) {
                    KickUserUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    KickUserUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    KickUserUseCase.Result.GameAlreadyStarted -> {
                        call.respond(HttpStatusCode.Conflict, "Game already started")
                        return@post
                    }

                    KickUserUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    KickUserUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }

                    KickUserUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    KickUserUseCase.Result.UserNotInRoom -> {
                        call.respond(HttpStatusCode.BadRequest, "User not in the room")
                        return@post
                    }

                    KickUserUseCase.Result.NotAllowed -> {
                        call.respond(HttpStatusCode.BadRequest, "You should use /leave instead of /kick")
                        return@post
                    }
                }
            }
        }

        authenticate {
            post("/start") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val id = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@post
                }
                val res = useCases.startGameUseCase(
                    userId = userId,
                    roomId = id,
                )

                when (res) {
                    StartGameUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    StartGameUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    StartGameUseCase.Result.GameAlreadyStarted -> {
                        call.respond(HttpStatusCode.Conflict, "Game already started")
                        return@post
                    }

                    StartGameUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    StartGameUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    StartGameUseCase.Result.NotEnoughPlayers -> {
                        call.respond(HttpStatusCode.BadRequest, "Not enough users to start playing")
                        return@post
                    }

                    StartGameUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }

                    StartGameUseCase.Result.ActiveRequests -> {
                        call.respond(HttpStatusCode.NotAcceptable, "You have active requests")
                        return@post
                    }
                }
            }
        }
        authenticate {
            post("/stop") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val id = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room id")
                    return@post
                }
                val res = useCases.stopGameUseCase(
                    userId = userId,
                    roomId = id,
                )
                when (res) {
                    StopGameUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    StopGameUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    StopGameUseCase.Result.GameAlreadyStopped -> {
                        call.respond(HttpStatusCode.Conflict, "Game already stopped")
                        return@post
                    }

                    StopGameUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    StopGameUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    StopGameUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }
                }
            }
        }
        authenticate {
            get("/info") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@get
                }

                val id = call.request.queryParameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest, "Wrong room name")
                    return@get
                }
                val res = useCases.getGameInfoUseCase(
                    userId = userId,
                    roomId = id,
                )

                when (res) {
                    GetGameInfoUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@get
                    }

                    GetGameInfoUseCase.Result.RoomNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@get
                    }

                    GetGameInfoUseCase.Result.UserNotExists -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@get
                    }

                    is GetGameInfoUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK, res.info)
                        return@get
                    }
                }
            }
        }

        authenticate {
            post("/accept") {
                val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asString() ?: run {
                    call.respond(HttpStatusCode.Unauthorized, "No access token provided")
                    return@post
                }

                val request = call.receiveNullable<AcceptUserRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val res = useCases.acceptUserUseCase(
                    selfId = userId,
                    userId = request.userId,
                    roomId = request.roomId,
                )

                when (res) {
                    AcceptUserUseCase.Result.Failed -> {
                        call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                        return@post
                    }

                    AcceptUserUseCase.Result.Forbidden -> {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    AcceptUserUseCase.Result.RoomNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "Room not exists")
                        return@post
                    }

                    AcceptUserUseCase.Result.Successful -> {
                        call.respond(HttpStatusCode.OK)
                        return@post
                    }

                    AcceptUserUseCase.Result.UserNotFound -> {
                        call.respond(HttpStatusCode.BadRequest, "User not exists")
                        return@post
                    }

                    AcceptUserUseCase.Result.UserNotInRoom -> {
                        call.respond(HttpStatusCode.BadRequest, "User not in room")
                        return@post
                    }
                }
            }
        }
    }
}