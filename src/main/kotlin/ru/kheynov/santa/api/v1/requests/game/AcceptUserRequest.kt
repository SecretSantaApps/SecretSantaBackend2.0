package ru.kheynov.santa.api.v1.requests.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcceptUserRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("room_id") val roomId: String,
)