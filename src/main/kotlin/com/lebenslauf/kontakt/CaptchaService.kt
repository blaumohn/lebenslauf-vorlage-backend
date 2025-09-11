package com.lebenslauf.kontakt

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.lebenslauf.common.Codierung
import com.lebenslauf.common.toBase64DataUrl
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.time.Duration.Companion.minutes
import com.lebenslauf.kontakt.repository.CaptchaSitzungRepository
import com.lebenslauf.kontakt.model.CaptchaSitzung
import org.springframework.transaction.annotation.Transactional
import com.lebenslauf.kontakt.model.CaptchaStatus
import java.util.UUID

sealed class CaptchaPruefungErgebnis {
  object Gueltig : CaptchaPruefungErgebnis()

  object Abgelaufen : CaptchaPruefungErgebnis()

  object Ungueltig : CaptchaPruefungErgebnis()
}

@Service
open class CaptchaService(
  private val kaptchaProducer: DefaultKaptcha,
  private val repository: CaptchaSitzungRepository
) {

  data class CaptchaRaetsel(
    val id: UUID, val bildDataUrl: String
  )

  @Transactional
  fun neuesCaptcha(): CaptchaRaetsel {
    val kaptchaText = kaptchaProducer.createText()
    val neueCaptchaSitzung = repository.saveAndFlush(
      CaptchaSitzung(kaptchaText)
    )

    val kaptchaBild = kaptchaProducer.createImage(kaptchaText)
    val bildDataUrl = kaptchaBild.toBase64DataUrl()

    return CaptchaRaetsel(neueCaptchaSitzung.id, bildDataUrl)
  }

  @Transactional
  fun pruefeCaptcha(
    id: String,
    antwort: String,
  ): CaptchaPruefungErgebnis {
    val captchaSitzung = repository.findById(id).orElse(null)
      ?: return CaptchaPruefungErgebnis.Ungueltig

    repository.incrementRequestCount(id)

    val erstellt = captchaSitzung.erstelltAm
      ?: return CaptchaPruefungErgebnis.Ungueltig

    val abgelaufen = erstellt.isBefore(
      OffsetDataTime.now().minusMinutes(5)
    )

    if (abgelaufen) return CaptchaPruefungErgebnis.Abgelaufen

    val isCaptchaMatch = (antwort == captchaSitzung.captchaText)

    return if (isCaptchaMatch) {
      alsGeloestSetzen(id)
      CaptchaPruefungErgebnis.Gueltig
    } else {
      CaptchaPruefungErgebnis.Ungueltig
    }
  }

  @Transactional
  fun alsGeloestSetzen(id: UUID) {
    repository.updateStatus(id, CaptchaStatus.GELOEST, CaptchaStatus.NEU)
  }

  @Transactional
  fun verbrauchen(id: UUID) {
    repository.updateStatus(
      id, CaptchaStatus.VERBRAUCHT, CaptchaStatus.NEU
    )
    repository.updateStatus(
      id, CaptchaStatus.VERBRAUCHT, CaptchaStatus.GELOEST
    )
  }
}
