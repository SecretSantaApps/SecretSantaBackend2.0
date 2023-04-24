package ru.kheynov.santa.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("include_external_user_ids") val usersId: List<String>,
    val contents: NotificationMessage,
    val headings: NotificationMessage,
    @SerialName("app_id") val appId: String,
)

@Serializable
data class NotificationMessage(
    val en: String,
    val ru: String? = null,
)