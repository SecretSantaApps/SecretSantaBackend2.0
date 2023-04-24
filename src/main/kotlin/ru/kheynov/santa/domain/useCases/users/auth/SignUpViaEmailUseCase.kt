package ru.kheynov.santa.domain.useCases.users.auth

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.entities.UserDTO
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.security.jwt.hashing.HashingService
import ru.kheynov.santa.security.jwt.token.*
import ru.kheynov.santa.utils.getRandomUserID
import ru.kheynov.santa.utils.getRandomUsername

class SignUpViaEmailUseCase : KoinComponent {

    private val usersRepository: UsersRepository by inject()
    private val tokenService: TokenService by inject()
    private val hashingService: HashingService by inject()
    private val tokenConfig: TokenConfig by inject()

    sealed interface Result {
        data class Successful(val tokenPair: TokenPair) : Result
        object Failed : Result
        object UserAlreadyExists : Result
        object AvatarNotFound : Result
    }

    suspend operator fun invoke(user: UserDTO.UserEmailSignUp): Result {
        if (usersRepository.getUserByEmail(user.email) != null) return Result.UserAlreadyExists
        val userId = getRandomUserID()
        val tokenPair = tokenService.generateTokenPair(tokenConfig, TokenClaim("userId", userId))

        val avatar = usersRepository.getAvatarById(user.avatar) ?: return Result.AvatarNotFound

        val resUser = UserDTO.User(
            userId = userId,
            username = user.username.ifEmpty { "Guest-${getRandomUsername()}" },
            email = user.email,
            passwordHash = hashingService.generateHash(user.password),
            authProvider = "local",
            address = user.address,
            avatar = avatar,
        )
        val registerUserResult = usersRepository.registerUser(resUser)
        val createUserRefreshTokenResult = usersRepository.createRefreshToken(
            userId = userId,
            clientId = user.clientId,
            refreshToken = RefreshToken(
                token = tokenPair.refreshToken.token,
                expiresAt = tokenPair.refreshToken.expiresAt,
            ),
        )

        return if (registerUserResult && createUserRefreshTokenResult) Result.Successful(tokenPair) else Result.Failed
    }
}