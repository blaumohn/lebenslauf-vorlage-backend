package com.lebenslauf.token

import org.springframework.stereotype.Service

@Service
class TokenService {
    private val validTokens = setOf("abc123", "demo456", "test789")

    fun isValid(token: String?): Boolean {
        return token != null && token in validTokens
    }
}
