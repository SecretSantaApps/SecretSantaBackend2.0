package ru.kheynov.santa.domain.useCases.rooms

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.entities.RoomDTO
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.utils.getRandomRoomID
import java.time.LocalDate

class CreateRoomUseCase : KoinComponent {
    private val roomsRepository: RoomsRepository by inject()
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        data class Successful(val room: RoomDTO.Room) : Result
        object UserNotExists : Result
        object Failed : Result
    }

    suspend operator fun invoke(
        userId: String,
        roomName: String,
        playableOwner: Boolean,
        date: LocalDate?,
        maxPrice: Int?,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        val room = RoomDTO.Room(
            name = roomName,
            id = getRandomRoomID(),
            date = date,
            ownerId = userId,
            playableOwner = playableOwner,
            maxPrice = maxPrice,
            gameStarted = false,
            membersCount = 1,
        )
        return if (roomsRepository.createRoom(room)) Result.Successful(room) else Result.Failed
    }
}