package ru.kheynov.santa.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kheynov.santa.domain.entities.UserDTO
import ru.kheynov.santa.utils.UpdateModel

interface GameRepository {
    val updates: Flow<UpdateModel>
    suspend fun addToRoom(roomId: String, userId: String, wishlist: String?): Boolean
    suspend fun deleteFromRoom(roomId: String, userId: String): Boolean
    suspend fun setRecipient(roomId: String, userId: String, recipientId: String): Boolean
    suspend fun deleteRecipients(roomId: String): Boolean
    suspend fun acceptUser(roomId: String, userId: String): Boolean
    suspend fun getUsersInRoom(roomId: String): List<UserDTO.UserRoomInfo>
    suspend fun getUsersRecipient(roomId: String, userId: String): String?
    suspend fun setGameState(roomId: String, state: Boolean): Boolean
    suspend fun checkUserInRoom(roomId: String, userId: String): Boolean
}