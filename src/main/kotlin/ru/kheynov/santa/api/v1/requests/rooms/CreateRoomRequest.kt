package ru.kheynov.santa.api.v1.requests.rooms

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.kheynov.santa.utils.LocalDateSerializer
import java.time.LocalDate

@Serializable
data class CreateRoomRequest(
    @SerialName("room_name")
    val name: String,
    val password: String?,
    val date:
        @Serializable(with = LocalDateSerializer::class)
        LocalDate?,
    @SerialName("max_price") val maxPrice: Int?,
    @SerialName("playable_owner") val playableOwner: Boolean,
    @SerialName("wishlist") val wishList: String?,
)