package com.lebenslauf.lebenslauf

import com.lebenslauf.token.TokenService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LebenslaufController(private val tokenService: TokenService) {
    @GetMapping("/api/lebenslauf")
    fun getLebenslauf(@RequestParam(required = false) token: String?): String {
        return if (tokenService.isValid(token)) {
            "vollständig"
        } else {
            "öffentlich"
        }
    }
}
