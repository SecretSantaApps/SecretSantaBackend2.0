package ru.kheynov.santa.domain.useCases.rooms

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.entities.RoomDTO
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository

class GetRoomDetailsUseCase : KoinComponent {
    private val roomsRepository: RoomsRepository by inject()
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        data class Successful(val room: RoomDTO.Room) : Result
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
        if (room.ownerId != userId) return Result.Forbidden
        return Result.Successful(room)
    }
}