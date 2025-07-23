package com.lebenslauf.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.imageio.ImageIO

@Component
class Codierung(
    @Value("\${security.hmac.secret}") private val geheimnis: String,
) {
    fun hmacSha256(text: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(geheimnis.toByteArray(), "HmacSHA256"))
        val hash = mac.doFinal(text.toByteArray())
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
    }

    fun bildAlsBase64(bild: BufferedImage): String {
        val out = ByteArrayOutputStream()
        ImageIO.write(bild, "png", out)
        val base64 = Base64.getEncoder().encodeToString(out.toByteArray())
        return "data:image/png;base64,$base64"
    }
}
