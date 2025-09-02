package com.lebenslauf.kontakt

import com.google.code.kaptcha.util.Config
import com.lebenslauf.common.Codierung
import com.lebenslauf.testutils.TestKaptcha
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.time.Instant
import java.util.Base64
import java.util.Properties
import javax.imageio.ImageIO

class CaptchaTokenServiceTest {
  private lateinit var dienst: CaptchaTokenService
  private lateinit var producer: TestKaptcha

  @BeforeEach
  fun setup() {
    val props =
      Properties().apply {
        val stream =
          javaClass.getResourceAsStream(
            "/kaptcha-unit.properties",
          )
        stream?.use { load(it) }
      }
    producer =
      TestKaptcha().apply {
        config = Config(props)
      }
    val codierung = Codierung("testgeheimnis")
    dienst =
      CaptchaTokenService(
        kaptchaProducer = producer,
        codierung = codierung,
      )
  }

  @Test
  fun `captcha kann erzeugt und verifiziert werden`() {
    val paket = dienst.neuesCaptcha()
    val captchaEingabe =
      producer.letzterText ?: error(
        "Text nicht verfügbar",
      )
    val token = paket.token
    val base64 = paket.bildBase64

    assertThat(base64).isNotBlank()
    assertThat(token).isNotBlank()

    val rawData =
      Base64.getDecoder().decode(
        base64.removePrefix("data:image/png;base64,"),
      )
    val image = ImageIO.read(ByteArrayInputStream(rawData))
    assertThat(image.width).isGreaterThan(50)
    assertThat(image.height).isGreaterThan(20)

    val ergebnis = dienst.pruefeToken(token, captchaEingabe)
    assertThat(ergebnis).isEqualTo(CaptchaPruefungErgebnis.Gueltig)
  }

  @Test
  fun `ungueltige eingabe wird abgelehnt`() {
    val paket = dienst.neuesCaptcha()
    val token = paket.token
    val ergebnis = dienst.pruefeToken(token, "FALSCH")
    assertThat(ergebnis).isEqualTo(CaptchaPruefungErgebnis.Ungueltig)
  }

  @Test
  fun `ungueltiger token wird abgelehnt`() {
    val timestamp = Instant.now().epochSecond
    val token = "abc|$timestamp|ungültig"
    val ergebnis = dienst.pruefeToken(token, "abc")
    assertThat(ergebnis).isEqualTo(CaptchaPruefungErgebnis.Ungueltig)
  }
}
