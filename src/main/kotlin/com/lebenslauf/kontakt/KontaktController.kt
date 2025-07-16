package com.lebenslauf.kontakt

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/kontakt")
class KontaktController {
    @PostMapping
    fun kontaktAbsenden(
        @RequestBody anfrage: KontaktAnfrageDto,
    ): ResponseEntity<String> {
        if (anfrage.botcheck != "42") {
            println("❌ Botcheck fehlgeschlagen für ${anfrage.email}")
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Botcheck fehlgeschlagen")
        }

        println(
            "✅ Neue Nachricht erhalten von ${anfrage.name} <${anfrage.email}>",
        )
        println("📝 Nachricht: ${anfrage.nachricht}")

        // Später: Email senden oder speichern

        return ResponseEntity.ok("Nachricht empfangen")
    }
}
