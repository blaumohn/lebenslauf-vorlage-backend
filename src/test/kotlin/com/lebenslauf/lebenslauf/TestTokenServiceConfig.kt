package com.lebenslauf.lebenslauf

import com.lebenslauf.token.TokenService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestTokenServiceConfig {

    @Bean
    fun tokenService(): TokenService {
        return TokenService()
    }
}
