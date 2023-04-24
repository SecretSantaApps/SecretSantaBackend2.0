package ru.kheynov.santa.api.v1.requests.users

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val username: String?,
    val address: String?,
    val avatar: Int?,
)