package com.lebenslauf.kontakt

import com.google.code.kaptcha.impl.DefaultKaptcha
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.imageio.ImageIO
import kotlin.time.Duration.Companion.minutes

@Service
class CaptchaTokenService(
    private val kaptchaProducer: DefaultKaptcha,
) {
    private val geheimnis = "geheimesTokenPasswort123".toByteArray()
    private val algorithmus = "HmacSHA256"

    data class CaptchaPaket(
        val bild: BufferedImage,
        val tokenFeld: String,
    ) {
        fun bildAlsBase64(): String {
            val out = ByteArrayOutputStream()
            ImageIO.write(bild, "png", out)
            val base64 = Base64.getEncoder().encodeToString(out.toByteArray())
            return "data:image/png;base64,$base64"
        }
    }

    fun neuesCaptcha(): CaptchaPaket {
        val captchaText = kaptchaProducer.createText()
        val timestamp = Instant.now().epochSecond.toString()
        val rohdaten = "$captchaText|$timestamp"
        val signatur = signiere(rohdaten)
        val token = "$captchaText|$timestamp|$signatur"

        val bild = kaptchaProducer.createImage(captchaText)

        return CaptchaPaket(
            bild = bild,
            tokenFeld = token,
        )
    }

    fun pruefeToken(token: String, eingabe: String): Boolean {
        val teile = token.split("|")
        if (teile.size != 3) return false

        val (text, zeitStr, sig) = teile
        val jetzt = Instant.now().epochSecond
        val zeitpunkt = zeitStr.toLongOrNull() ?: return false

        if ((jetzt - zeitpunkt) > 5.minutes.inWholeSeconds) return false

        val rohdaten = "$text|$zeitStr"
        val erwarteteSignatur = signiere(rohdaten)

        return sig == erwarteteSignatur && eingabe == text
    }

    private fun signiere(eingabe: String): String {
        val mac = Mac.getInstance(algorithmus)
        mac.init(SecretKeySpec(geheimnis, algorithmus))
        val hash = mac.doFinal(eingabe.toByteArray())
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
    }
}
