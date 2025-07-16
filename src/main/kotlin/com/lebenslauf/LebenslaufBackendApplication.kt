package com.lebenslauf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class LebenslaufBackendApplication

fun main(args: Array<String>) {
    runApplication<LebenslaufBackendApplication>(*args)
}
