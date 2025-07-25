package com.lebenslauf.kontakt.config

import com.lebenslauf.kontakt.model.Dummy
import com.lebenslauf.kontakt.repository.DummyRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dev")
class TestH2Runner {

    @Bean
    fun testRunner(dummyRepository: DummyRepository) = CommandLineRunner {
        dummyRepository.save(Dummy("test", "Hallo H2!"))
        println("Inhalt: ${dummyRepository.findAll()}")
    }
}
