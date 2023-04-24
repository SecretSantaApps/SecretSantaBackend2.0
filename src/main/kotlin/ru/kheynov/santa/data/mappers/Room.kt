package ru.kheynov.santa.data.mappers

import ru.kheynov.santa.data.entities.Room
import ru.kheynov.santa.domain.entities.RoomDTO

fun Room.mapToRoom(): RoomDTO.Room = RoomDTO.Room(
    name = this.name,
    id = this.id,
    date = this.date,
    ownerId = this.ownerId,
    playableOwner = this.playableOwner,
    maxPrice = this.maxPrice,
    gameStarted = this.gameStarted,
    membersCount = 1,
)