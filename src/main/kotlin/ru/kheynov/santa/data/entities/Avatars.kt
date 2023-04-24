package ru.kheynov.santa.data.entities

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

interface Avatar : Entity<Avatar> {
    companion object : Entity.Factory<Avatar>()

    var id: Int
    var image: String
}

object Avatars : Table<Avatar>("avatars") {
    var id = int("id").primaryKey().bindTo(Avatar::id)
    var image = text("image").bindTo(Avatar::image)
}