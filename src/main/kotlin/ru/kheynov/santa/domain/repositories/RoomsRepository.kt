package ru.kheynov.santa.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kheynov.santa.domain.entities.RoomDTO
import ru.kheynov.santa.domain.entities.RoomDTO.Room
import ru.kheynov.santa.domain.entities.RoomDTO.RoomInfo
import ru.kheynov.santa.utils.UpdateModel

interface RoomsRepository {

    val updates: Flow<UpdateModel>

    suspend fun createRoom(room: Room): Boolean
    suspend fun deleteRoomById(id: String): Boolean
    suspend fun getRoomById(id: String): Room?
    suspend fun updateRoomById(id: String, newRoomData: RoomDTO.RoomUpdate): Boolean
    suspend fun getUserRooms(userId: String): List<RoomInfo>
}