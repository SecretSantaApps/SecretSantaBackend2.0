package ru.kheynov.santa.domain.entities

import io.ktor.server.auth.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface UserDTO {
    @Serializable
    data class User(
        @SerialName("user_id") val userId: String,
        val username: String,
        val passwordHash: String?,
        @SerialName("auth_provider") val authProvider: String,
        val email: String,
        val address: String?,
        val avatar: String,
    ) : UserDTO

    @Serializable
    data class UserEmailSignUp(
        val username: String,
        val password: String,
        val email: String,
        @SerialName("client_id") val clientId: String,
        val address: String?,
        val avatar: Int,
    ) : UserDTO

    @Serializable
    data class UserInfo(
        @SerialName("user_id") val userId: String,
        @SerialName("client_ids") val clientIds: List<String>? = null,
        val username: String,
        val email: String,
        val address: String?,
        val avatar: String,
    ) : UserDTO

    @Serializable
    data class UserRoomInfo(
        @SerialName("user_id") val userId: String,
        val username: String,
        val address: String?,
        val wishlist: String?,
        val avatar: String,
        val accepted: Boolean? = false, // nullable because it could be null for non-admin requesters
    ) : UserDTO

    data class RefreshTokenInfo(
        val userId: String,
        val clientId: String,
        val token: String,
        val expiresAt: Long,
    ) : UserDTO

    data class UpdateUser(
        val username: String?,
        val address: String?,
        val avatar: Int?,
    ) : UserDTO
}