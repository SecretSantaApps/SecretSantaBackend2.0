package ru.kheynov.santa.api.v1.requests.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomRequest(
    @SerialName("room_id") val roomId: String,
    val wishlist: String?,
)