package ru.kheynov.santa.domain.repositories

import ru.kheynov.santa.domain.entities.AvatarDTO
import ru.kheynov.santa.domain.entities.UserDTO
import ru.kheynov.santa.security.jwt.token.RefreshToken

interface UsersRepository {
    suspend fun registerUser(user: UserDTO.User): Boolean
    suspend fun deleteUserByID(userId: String): Boolean
    suspend fun getUserByID(userId: String): UserDTO.UserInfo?
    suspend fun updateUserByID(userId: String, update: UserDTO.UpdateUser): Boolean
    suspend fun getUserByEmail(email: String): UserDTO.User?

    suspend fun updateUserRefreshToken(
        userId: String,
        clientId: String,
        newRefreshToken: String,
        refreshTokenExpiration: Long,
    ): Boolean

    suspend fun getRefreshToken(oldRefreshToken: String): UserDTO.RefreshTokenInfo?

    suspend fun getRefreshToken(userId: String, clientId: String): UserDTO.RefreshTokenInfo?

    suspend fun createRefreshToken(
        userId: String,
        clientId: String,
        refreshToken: RefreshToken,
    ): Boolean

    suspend fun getAvailableAvatars(): List<AvatarDTO>

    suspend fun getAvatarById(avatarId: Int): String?
}