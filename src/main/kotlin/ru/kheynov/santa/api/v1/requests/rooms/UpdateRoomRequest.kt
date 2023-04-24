package ru.kheynov.santa.api.v1.requests.rooms

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kheynov.santa.utils.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class UpdateRoomRequest(
    @SerialName("room_name") val name: String?,
    @Serializable(with = LocalDateSerializer::class) val date: LocalDate?,
    @SerialName("max_price") val maxPrice: Int?,
)