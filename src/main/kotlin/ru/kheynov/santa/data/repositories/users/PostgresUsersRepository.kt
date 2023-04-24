package ru.kheynov.santa.data.repositories.users

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import ru.kheynov.santa.data.entities.Avatars
import ru.kheynov.santa.data.entities.RefreshTokens
import ru.kheynov.santa.data.entities.User
import ru.kheynov.santa.data.entities.Users
import ru.kheynov.santa.data.mappers.mapToUser
import ru.kheynov.santa.data.mappers.toDataRefreshToken
import ru.kheynov.santa.data.mappers.toRefreshTokenInfo
import ru.kheynov.santa.domain.entities.AvatarDTO
import ru.kheynov.santa.domain.entities.UserDTO
import ru.kheynov.santa.domain.repositories.UsersRepository
import ru.kheynov.santa.security.jwt.token.RefreshToken

class PostgresUsersRepository(
    private val database: Database,
) : UsersRepository {
    override suspend fun registerUser(user: UserDTO.User): Boolean {
        val avatar = database.sequenceOf(Avatars).find { it.image eq user.avatar } ?: return false
        val affectedRows = database.sequenceOf(Users).add(
            User {
                userId = user.userId
                name = user.username
                email = user.email
                passwordHash = user.passwordHash
                authProvider = user.authProvider
                address = user.address
                this.avatar = avatar
            },
        )
        return affectedRows == 1
    }

    override suspend fun getUserByID(userId: String): UserDTO.UserInfo? {
        val clientIds = database.from(RefreshTokens)
            .selectDistinct(RefreshTokens.clientId)
            .where { RefreshTokens.userId eq userId }
            .map { row -> row[RefreshTokens.clientId]!! }

        return database.from(Users)
            .innerJoin(Avatars, on = Users.avatar eq Avatars.id)
            .select(
                Users.userId,
                Users.name,
                Users.email,
                Users.address,
                Users.authProvider,
                Avatars.image,
            )
            .where(Users.userId eq userId).limit(1).map { row ->
                UserDTO.UserInfo(
                    userId = row[Users.userId]!!,
                    username = row[Users.name]!!,
                    email = row[Users.email]!!,
                    address = row[Users.address],
                    avatar = row[Avatars.image]!!,
                    clientIds = clientIds,
                )
            }.firstOrNull()
    }

    override suspend fun deleteUserByID(userId: String): Boolean {
        val affectedRows = database.sequenceOf(Users).find { user -> user.userId eq userId }?.delete()
        return affectedRows == 1
    }

    override suspend fun updateUserByID(userId: String, update: UserDTO.UpdateUser): Boolean {
        val foundUser = database.sequenceOf(Users).find { it.userId eq userId } ?: return false

        if (update.username != null) foundUser.name = update.username

        foundUser.address = update.address

        if (update.avatar != null) {
            val foundAvatar = database.sequenceOf(Avatars).find { it.id eq update.avatar } ?: return false
            foundUser.avatar = foundAvatar
        }
//        if (update.email != null) foundUser.email = update.email
        val affectedRows = foundUser.flushChanges()
        return affectedRows == 1
    }

    override suspend fun getUserByEmail(email: String): UserDTO.User? {
        val foundUser = database.sequenceOf(Users).find { it.email eq email } ?: return null
        return foundUser.mapToUser()
    }

    override suspend fun updateUserRefreshToken(
        userId: String,
        clientId: String,
        newRefreshToken: String,
        refreshTokenExpiration: Long,
    ): Boolean {
        val foundUser = database.sequenceOf(RefreshTokens).find { (it.userId eq userId) and (it.clientId eq clientId) }
            ?: return false
        foundUser.refreshToken = newRefreshToken
        foundUser.expiresAt = refreshTokenExpiration
        val affectedRows = foundUser.flushChanges()
        return affectedRows == 1
    }

    override suspend fun getRefreshToken(oldRefreshToken: String): UserDTO.RefreshTokenInfo? {
        return database.sequenceOf(RefreshTokens).find { oldRefreshToken eq it.refreshToken }?.toRefreshTokenInfo()
    }

    override suspend fun getRefreshToken(userId: String, clientId: String): UserDTO.RefreshTokenInfo? {
        return database.sequenceOf(RefreshTokens).find { (userId eq it.userId) and (clientId eq it.clientId) }
            ?.toRefreshTokenInfo()
    }

    override suspend fun createRefreshToken(userId: String, clientId: String, refreshToken: RefreshToken): Boolean {
        val affectedRows = database.sequenceOf(RefreshTokens).add(refreshToken.toDataRefreshToken(userId, clientId))
        return affectedRows == 1
    }

    override suspend fun getAvailableAvatars(): List<AvatarDTO> =
        database.from(Avatars).select(Avatars.id, Avatars.image).map { row ->
            AvatarDTO(
                id = row[Avatars.id]!!,
                image = row[Avatars.image]!!,
            )
        }

    override suspend fun getAvatarById(avatarId: Int): String? {
        return database.sequenceOf(Avatars).find { it.id eq avatarId }?.image
    }
}