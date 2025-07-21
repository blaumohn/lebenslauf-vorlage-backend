package com.lebenslauf.kontakt

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Properties

class CaptchaTokenServiceTest {

    private lateinit var dienst: CaptchaTokenService

    @BeforeEach
    fun setup() {
        val props = Properties().apply {
            val stream = javaClass.getResourceAsStream(
                "/kaptcha-unit.properties",
            )
            stream?.use { load(it) }
        }
        val producer = DefaultKaptcha().apply {
            config = Config(props)
        }
        dienst = CaptchaTokenService(producer)
    }

    @Test
    fun `captcha kann erzeugt und verifiziert werden`() {
        val paket = dienst.neuesCaptcha()

        val token = paket.tokenFeld
        val bild = paket.bild

        assertThat(bild).isNotNull
        assertThat(bild.width).isGreaterThan(50)
        assertThat(bild.height).isGreaterThan(20)
        assertThat(token).isNotEmpty

        val teile = token.split("|")
        assertThat(teile).hasSize(3)

        val captchaText = teile[0]
        val gueltig = dienst.pruefeToken(token, captchaText)

        assertThat(gueltig).isTrue()
    }

    @Test
    fun `ungueltige eingabe wird abgelehnt`() {
        val paket = dienst.neuesCaptcha()
        val token = paket.tokenFeld

        val falsch = dienst.pruefeToken(token, "FALSCH")

        assertThat(falsch).isFalse()
    }

    @Test
    fun `ungueltiger token wird abgelehnt`() {
        val token = "abc|1234567890|ungültig"
        val gueltig = dienst.pruefeToken(token, "abc")

        assertThat(gueltig).isFalse()
    }
}
