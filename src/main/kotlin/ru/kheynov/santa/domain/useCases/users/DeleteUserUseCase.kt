package ru.kheynov.santa.domain.useCases.users

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.repositories.UsersRepository

class DeleteUserUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()

    sealed interface Result {
        object Successful : Result
        object Failed : Result
        object UserNotExists : Result
    }

    suspend operator fun invoke(userId: String): Result {
        if (usersRepository.getUserByID(userId) == null) return Result.UserNotExists
        return if (usersRepository.deleteUserByID(userId)) Result.Successful else Result.Failed
    }
}