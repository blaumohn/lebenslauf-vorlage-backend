package com.lebenslauf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class LebenslaufBackendApplication

fun main(args: Array<String>) {
    runApplication<LebenslaufBackendApplication>(*args)
}
