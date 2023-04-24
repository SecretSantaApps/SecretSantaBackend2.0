package ru.kheynov.santa.api.v1.requests.users.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpViaEmailRequest(
    val username: String,
    val email: String,
    val password: String,
    val address: String?,
    val avatar: Int = 1,
)