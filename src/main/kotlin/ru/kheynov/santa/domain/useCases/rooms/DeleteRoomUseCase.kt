package ru.kheynov.santa.domain.useCases.rooms

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository

class DeleteRoomUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()

    sealed interface Result {
        object Successful : Result
        object UserNotExists : Result
        object RoomNotExists : Result
        object Failed : Result
        object Forbidden : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotExists
        if (room.ownerId != userId) return Result.Forbidden
        return if (roomsRepository.deleteRoomById(roomId)) Result.Successful else Result.Failed
    }
}