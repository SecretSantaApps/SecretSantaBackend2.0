package ru.kheynov.santa.domain.useCases.users

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.entities.UserDTO
import ru.kheynov.santa.domain.repositories.UsersRepository

class UpdateUserUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object UserNotExists : Result
        object AvatarNotFound : Result
    }

    suspend operator fun invoke(
        userId: String,
        update: UserDTO.UpdateUser,
    ): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        if (update.avatar != null) if (usersRepository.getAvatarById(update.avatar) == null) return Result.AvatarNotFound
        return if (usersRepository.updateUserByID(
                userId,
                update,
            )
        ) {
            Result.Successful
        } else {
            Result.Failed
        }
    }
}