package ru.kheynov.santa.security.jwt.hashing

import at.favre.lib.crypto.bcrypt.BCrypt

class BcryptHashingService : HashingService {
    override fun generateHash(password: String): String = BCrypt.withDefaults().hashToString(12, password.toCharArray())

    override fun verify(password: String, hash: String): BCrypt.Result =
        BCrypt.verifyer().verify(password.toCharArray(), hash.toByteArray())
}