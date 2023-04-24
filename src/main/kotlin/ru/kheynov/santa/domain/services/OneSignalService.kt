package ru.kheynov.santa.domain.services

import ru.kheynov.santa.domain.entities.Notification
import java.lang.System.getenv

interface OneSignalService {
    suspend fun sendNotification(notification: Notification): Boolean

    companion object {
        val APP_ID: String = getenv("ONESIGNAL_APP_ID")
        const val BASE_URL = "https://onesignal.com/api/v1/notifications"
        val API_KEY: String = getenv("ONESIGNAL_API_KEY")
    }
}