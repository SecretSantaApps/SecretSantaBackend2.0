package ru.kheynov.santa.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    var userId: String
    var name: String
    var email: String
    var passwordHash: String?
    var authProvider: String
    var address: String?
    var avatar: Avatar
}

object Users : Table<User>("users") {
    var userId = text("user_id").primaryKey().bindTo(User::userId)
    var name = text("name").bindTo(User::name)
    var email = text("email").bindTo(User::email)
    var passwordHash = text("password_hash").bindTo(User::passwordHash)
    val authProvider = text("auth_provider").bindTo(User::authProvider)
    var address = text("address").bindTo(User::address)
    var avatar = int("avatar").references(Avatars) { it.avatar }
}