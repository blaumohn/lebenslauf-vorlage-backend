package com.lebenslauf.kontakt.config

import com.lebenslauf.kontakt.model.CaptchaSitzung
import com.lebenslauf.kontakt.repository.CaptchaSitzungRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.LocalDateTime

@Configuration
@Profile("dev")
class TestTabelleRunner {

    @Bean
    fun testCaptchaRunner(captchaRepo: CaptchaSitzungRepository) =
        CommandLineRunner {
            // var id = UUID.randomUUID()
            captchaRepo.saveAndFlush(
                CaptchaSitzung(
                    // id = id,
                    captchaText = "TEST123",
                    datum = LocalDateTime.now(),
                ),
            )
            println("Inhalt: ${captchaRepo.findAll()}")
        }
}
