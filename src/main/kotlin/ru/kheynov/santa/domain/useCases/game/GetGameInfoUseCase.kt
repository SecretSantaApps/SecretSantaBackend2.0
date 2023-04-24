package ru.kheynov.santa.domain.useCases.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.api.v1.responses.InfoDetails
import ru.kheynov.santa.domain.repositories.GameRepository
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository

class GetGameInfoUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val gameRepository: GameRepository by inject()

    sealed interface Result {
        data class Successful(val info: InfoDetails) : Result
        object UserNotExists : Result
        object RoomNotExists : Result
        object Forbidden : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotExists
        var users = gameRepository.getUsersInRoom(roomId)

        val isAdmin = userId == room.ownerId

        if (users.find { it.userId == userId } == null) return Result.Forbidden

        if (!isAdmin) {
            users = users.map {
                if (it.userId != userId) {
                    it.copy(accepted = null)
                } else {
                    it
                }
            }
        }

        val info = InfoDetails(
            roomId = room.id,
            roomName = room.name,
            ownerId = room.ownerId,
            date = room.date,
            maxPrice = room.maxPrice,
            users = users,
            recipient = gameRepository.getUsersRecipient(roomId, userId),
        )
        return Result.Successful(info)
    }
}