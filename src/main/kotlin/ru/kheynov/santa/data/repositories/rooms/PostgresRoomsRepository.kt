package ru.kheynov.santa.data.repositories.rooms

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import ru.kheynov.santa.data.entities.RoomMembers
import ru.kheynov.santa.data.entities.Rooms
import ru.kheynov.santa.data.mappers.mapToRoom
import ru.kheynov.santa.domain.entities.RoomDTO
import ru.kheynov.santa.domain.entities.RoomDTO.Room
import ru.kheynov.santa.domain.entities.RoomDTO.RoomInfo
import ru.kheynov.santa.domain.repositories.RoomsRepository
import ru.kheynov.santa.utils.UpdateModel

class PostgresRoomsRepository(
    private val database: Database,
) : RoomsRepository {

    private val _updates = MutableSharedFlow<UpdateModel>()
    override val updates: Flow<UpdateModel>
        get() = _updates

    override suspend fun createRoom(room: Room): Boolean {
        var newRoom = ru.kheynov.santa.data.entities.Room {
            id = room.id
            name = room.name
            date = room.date
            maxPrice = room.maxPrice
            ownerId = room.ownerId
            playableOwner = room.playableOwner
            gameStarted = false
        }
        var affectedRows = database.sequenceOf(Rooms).add(newRoom)

        if (affectedRows != 1) { // if failed, try to change UUID
            newRoom = newRoom.copy()
            newRoom.id = room.id + 1
            affectedRows = database.sequenceOf(Rooms).add(newRoom)
        }

        return affectedRows == 1
    }

    override suspend fun deleteRoomById(id: String): Boolean {
        val affectedRows = database.sequenceOf(Rooms).find { it.id eq id }?.delete()
        return affectedRows == 1
    }

    override suspend fun getRoomById(id: String): Room? {
        val membersCount = database.sequenceOf(RoomMembers).filter { it.roomId eq id }
            .aggregateColumns { count(it.userId) }
        return database.sequenceOf(Rooms).find { it.id eq id }?.mapToRoom()?.copy(membersCount = membersCount ?: 1)
    }

    override suspend fun updateRoomById(id: String, newRoomData: RoomDTO.RoomUpdate): Boolean {
        val room = database.sequenceOf(Rooms).find { it.id eq id } ?: return false
        room.name = newRoomData.name ?: room.name
        room.date = newRoomData.date ?: room.date
        room.maxPrice = newRoomData.maxPrice ?: room.maxPrice
        val affectedRows = room.flushChanges()
        return if (affectedRows == 1) {
            _updates.emit(UpdateModel.RoomUpdate(id, newRoomData))
            true
        } else {
            false
        }
    }

    override suspend fun getUserRooms(userId: String): List<RoomInfo> =
        database.from(Rooms)
            .innerJoin(RoomMembers, on = Rooms.id eq RoomMembers.roomId).select(
                Rooms.name,
                Rooms.id,
                Rooms.date,
                Rooms.ownerId,
                Rooms.ownerId,
                Rooms.gameStarted,
                RoomMembers.accepted,
                Rooms.playableOwner,
            ).where {
                RoomMembers.userId eq userId
            }.map { room ->
                val membersCount = database.sequenceOf(RoomMembers).filter { it.roomId eq (room[Rooms.id] ?: "") }
                    .aggregateColumns { count(it.userId) }
                RoomInfo(
                    name = room[Rooms.name] ?: "",
                    id = room[Rooms.id] ?: "",
                    date = room[Rooms.date],
                    ownerId = room[Rooms.ownerId] ?: "",
                    maxPrice = room[Rooms.maxPrice],
                    gameStarted = room[Rooms.gameStarted] ?: false,
                    membersCount = membersCount ?: 0,
                    accepted = room[RoomMembers.accepted]!!,
                    playableOwner = room[Rooms.playableOwner]!!,
                )
            }
}