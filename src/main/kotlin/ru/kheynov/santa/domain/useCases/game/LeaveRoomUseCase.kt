package ru.kheynov.santa.domain.useCases.game

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.repositories.GameRepository
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository

class LeaveRoomUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()
    private val gameRepository: GameRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object UserNotInRoom : Result
        object RoomNotFound : Result
        object UserNotFound : Result
        object GameAlreadyStarted : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotFound
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotFound
        if (gameRepository.getUsersInRoom(roomId).find { it.userId == userId } == null) return Result.UserNotInRoom
        if (room.gameStarted) return Result.GameAlreadyStarted

        var res = gameRepository.deleteFromRoom(
            roomId = roomId,
            userId = userId,
        )

        if (room.ownerId == userId) {
            res = res && roomsRepository.deleteRoomById(roomId)
        }

        return if (res) Result.Successful else Result.Failed
    }
}