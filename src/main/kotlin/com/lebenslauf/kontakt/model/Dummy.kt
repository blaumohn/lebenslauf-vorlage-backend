package com.lebenslauf.kontakt.model

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Dummy(
    @Id
    val id: String,
    val message: String,
)
