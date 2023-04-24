package ru.kheynov.santa.security.jwt.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenPair(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: RefreshToken,
)

@Serializable
data class RefreshToken(
    val token: String,
    @SerialName("expiration") val expiresAt: Long,
)