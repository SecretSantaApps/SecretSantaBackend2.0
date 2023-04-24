package ru.kheynov.santa.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text

interface RefreshToken : Entity<RefreshToken> {
    companion object : Entity.Factory<RefreshToken>()

    var userId: String
    var clientId: String
    var refreshToken: String
    var expiresAt: Long
}

object RefreshTokens : Table<RefreshToken>("refresh_tokens") {
    var userId = text("user_id").primaryKey().bindTo(RefreshToken::userId)
    var clientId = text("client_id").primaryKey().bindTo(RefreshToken::clientId)
    var refreshToken = text("token").bindTo(RefreshToken::refreshToken)
    var refreshTokenExpiration = long("expires_at").bindTo(RefreshToken::expiresAt)
}