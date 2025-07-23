package com.lebenslauf.kontakt

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.lebenslauf.common.Codierung
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

sealed class CaptchaPruefungErgebnis {
    object Gueltig : CaptchaPruefungErgebnis()
    object Abgelaufen : CaptchaPruefungErgebnis()
    object Ungueltig : CaptchaPruefungErgebnis()
}

@Service
open class CaptchaTokenService(
    private val kaptchaProducer: DefaultKaptcha,
    private val codierung: Codierung,
) {

    data class CaptchaPacket(
        val token: String,
        val bildBase64: String,
    )

    fun neuesCaptcha(): CaptchaPacket {
        val captchaText = kaptchaProducer.createText()
        val captchaTextHash = codierung.hmacSha256(captchaText)
        val timestamp = Instant.now().epochSecond
        val rawData = "$captchaTextHash|$timestamp"
        val signatur = codierung.hmacSha256(rawData)
        val token = CaptchaToken(captchaTextHash, timestamp, signatur).toRaw()

        val bild = kaptchaProducer.createImage(captchaText)
        val bildBase64 = codierung.bildAlsBase64(bild)

        return CaptchaPacket(
            token = token,
            bildBase64 = bildBase64,
        )
    }

    fun pruefeToken(
        tokenRaw: String,
        captchaEingabe: String,
    ): CaptchaPruefungErgebnis {
        val token = try {
            CaptchaToken.parse(tokenRaw)
        } catch (e: IllegalArgumentException) {
            return CaptchaPruefungErgebnis.Ungueltig
        }

        val isExpired =
            Instant.now().epochSecond - token.timestamp >
                5.minutes.inWholeSeconds
        if (isExpired) return CaptchaPruefungErgebnis.Abgelaufen

        val expectedSignatur = codierung.hmacSha256(
            "${token.hash}|${token.timestamp}",
        )
        val isSignaturValid = expectedSignatur == token.signature
        val isCaptchaMatch = codierung.hmacSha256(captchaEingabe) == token.hash

        return if (isSignaturValid && isCaptchaMatch) {
            CaptchaPruefungErgebnis.Gueltig
        } else {
            CaptchaPruefungErgebnis.Ungueltig
        }
    }
} 
