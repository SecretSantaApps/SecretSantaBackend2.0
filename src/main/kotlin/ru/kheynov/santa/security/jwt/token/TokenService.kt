package ru.kheynov.santa.security.jwt.token

interface TokenService {
    fun generateTokenPair(config: TokenConfig, vararg claims: TokenClaim): TokenPair
}