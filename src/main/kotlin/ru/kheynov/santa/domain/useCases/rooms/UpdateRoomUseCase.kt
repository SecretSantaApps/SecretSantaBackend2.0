package ru.kheynov.santa.domain.useCases.rooms

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.entities.RoomDTO
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository

class UpdateRoomUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val roomsRepository: RoomsRepository by inject()

    sealed interface Result {
        object Successful : Result
        object UserNotExists : Result
        object RoomNotExists : Result
        object Forbidden : Result
        object Failed : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomId: String,
        roomUpdate: RoomDTO.RoomUpdate,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        val room = roomsRepository.getRoomById(roomId) ?: return Result.RoomNotExists
        if (room.ownerId != userId) return Result.Forbidden
        return if (roomsRepository.updateRoomById(
                roomId,
                roomUpdate,
            )
        ) {
            Result.Successful
        } else {
            Result.Failed
        }
    }
}