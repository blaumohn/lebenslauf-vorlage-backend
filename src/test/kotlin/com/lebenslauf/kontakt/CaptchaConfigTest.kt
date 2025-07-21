package com.lebenslauf.kontakt

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage
import java.util.Properties

class CaptchaConfigTest {
    val props = Properties().apply {
        val stream = javaClass.getResourceAsStream("/kaptcha-unit.properties")
        stream?.use { load(it) }
    }
    val erzeuger = DefaultKaptcha().apply {
        config = Config(props)
    }

    @Test
    fun `captcha bild kann erzeugt werden`() {
        val zeichenkette = "TEST1"

        // Bild erzeugen
        val bild: BufferedImage = erzeuger.createImage(zeichenkette)

        // Test: Bild ist nicht null
        assertThat(bild)
            .describedAs("Das erzeugte Captcha-Bild darf nicht null sein.")
            .isNotNull

        // Test: Bild hat sinnvolle Breite und Höhe
        assertThat(bild.width)
            .describedAs("Breite des Captcha-Bildes sollte > 50 sein.")
            .isGreaterThan(50)

        assertThat(bild.height)
            .describedAs("Höhe des Captcha-Bildes sollte > 20 sein.")
            .isGreaterThan(20)

        // Optional: Farbraum prüfen
        assertThat(bild.colorModel.numColorComponents)
            .describedAs(
                "Captcha-Bild sollte mindestens 3 Farbkanäle haben (RGB).",
            )
            .isGreaterThanOrEqualTo(3)
    }
}
