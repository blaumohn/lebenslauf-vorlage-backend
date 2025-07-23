package com.lebenslauf.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage

class CodierungTest {

    private val geheimnis = "testgeheimnis"
    private val codierung = Codierung(geheimnis)

    @Test
    fun `hmacSha256 erzeugt deterministisches ergebnis`() {
        val text = "captcha123"
        val hash1 = codierung.hmacSha256(text)
        val hash2 = codierung.hmacSha256(text)

        assertThat(hash1).isEqualTo(hash2)
        assertThat(hash1).isNotBlank
        assertThat(hash1).doesNotContain("=") // weil ohne Padding
    }

    @Test
    fun `hmacSha256 reagiert auf geheimnis aenderung`() {
        val text = "captcha123"
        val andereCodierung = Codierung("anderesGeheimnis")

        val hash1 = codierung.hmacSha256(text)
        val hash2 = andereCodierung.hmacSha256(text)

        assertThat(hash1).isNotEqualTo(hash2)
    }

    @Test
    fun `bildAlsBase64 gibt base64 PNG zurueck`() {
        val bild = BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB).apply {
            graphics.color = Color.RED
            graphics.fillRect(0, 0, width, height)
        }

        val base64 = codierung.bildAlsBase64(bild)

        assertThat(base64).startsWith("data:image/png;base64,")
        val raw = base64.removePrefix("data:image/png;base64,")

        val decoded = java.util.Base64.getDecoder().decode(raw)
        assertThat(decoded).isNotEmpty
    }
}
