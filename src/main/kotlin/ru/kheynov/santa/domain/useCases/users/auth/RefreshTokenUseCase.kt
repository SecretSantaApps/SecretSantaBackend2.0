package ru.kheynov.santa.domain.useCases.users.auth

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.security.jwt.token.TokenClaim
import ru.kheynov.santa.security.jwt.token.TokenConfig
import ru.kheynov.santa.security.jwt.token.TokenPair
import ru.kheynov.santa.security.jwt.token.TokenService

class RefreshTokenUseCase : KoinComponent {
    private val usersRepository: UsersRepository by inject()
    private val tokenService: TokenService by inject()
    private val tokenConfig: TokenConfig by inject()

    sealed interface Result {
        data class Success(val tokenPair: TokenPair) : Result
        object NoRefreshTokenFound : Result
        object RefreshTokenExpired : Result
        object Forbidden : Result
        object Failed : Result
    }

    suspend operator fun invoke(
        oldRefreshToken: String,
    ): Result {
        val refreshTokenInfo =
            usersRepository.getRefreshToken(oldRefreshToken) ?: return Result.NoRefreshTokenFound
        if (refreshTokenInfo.token != oldRefreshToken) return Result.Forbidden
        if (refreshTokenInfo.expiresAt < System.currentTimeMillis()) return Result.RefreshTokenExpired

        val newTokenPair = tokenService.generateTokenPair(tokenConfig, TokenClaim("userId", refreshTokenInfo.userId))
        val updateRefreshTokenResult = usersRepository.updateUserRefreshToken(
            newRefreshToken = newTokenPair.refreshToken.token,
            refreshTokenExpiration = newTokenPair.refreshToken.expiresAt,
            userId = refreshTokenInfo.userId,
            clientId = refreshTokenInfo.clientId,
        )
        return if (updateRefreshTokenResult) Result.Success(newTokenPair) else Result.Failed
    }
}