package ru.kheynov.santa.domain.useCases.utils

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.entities.AvatarDTO
import ru.kheynov.santa.domain.repositories.UsersRepository

class GetAvatarsList : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    suspend operator fun invoke(): List<AvatarDTO> =
        usersRepository.getAvailableAvatars()
}