package ru.kheynov.santa.data.mappers

import ru.kheynov.santa.data.entities.User
import ru.kheynov.santa.domain.entities.UserDTO

// fun User.mapToUserInfo(): UserDTO.UserInfo = UserDTO.UserInfo(
//    userId = this.userId,
//    username = this.name,
//    email = this.email,
//    address = this.address,
//    avatar = this.avatar.image,
//    clientIds = null
// )

fun User.mapToUser(): UserDTO.User = UserDTO.User(
    userId = this.userId,
    username = this.name,
    email = this.email,
    passwordHash = this.passwordHash,
    authProvider = this.authProvider,
    address = this.address,
    avatar = this.avatar.image,
)