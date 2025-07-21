package com.lebenslauf.testutils

import com.lebenslauf.kontakt.KontaktAnfrageDto

object TestFixtures {
    fun createKontaktAnfrageDto(
        name: String = "Anna",
        email: String = "anna@example.com",
        nachricht: String = "Ich interessiere mich für dein Profil.",
        botcheck: String = "42",
    ): KontaktAnfrageDto = KontaktAnfrageDto(
        name = name,
        email = email,
        nachricht = nachricht,
    )
}
