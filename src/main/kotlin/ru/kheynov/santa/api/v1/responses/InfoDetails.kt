package ru.kheynov.santa.api.v1.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kheynov.santa.domain.entities.UserDTO
import ru.kheynov.santa.utils.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class InfoDetails(
    @SerialName("room_id") val roomId: String,
    @SerialName("room_name") val roomName: String,
    @SerialName("owner_id") val ownerId: String,
    val date:
        @Serializable(with = LocalDateSerializer::class)
        LocalDate?,
    @SerialName("max_price") val maxPrice: Int?,
    val users: List<UserDTO.UserRoomInfo>,
    val recipient: String? = null,
)