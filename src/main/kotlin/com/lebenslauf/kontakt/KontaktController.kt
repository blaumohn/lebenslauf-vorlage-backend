package com.lebenslauf.kontakt

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/kontakt")
class KontaktController(private val captchaService: CaptchaService) {
  @GetMapping
  fun formularAnzeigen(model: Model): String = neuesFormular(
    model, KontaktAnfrageDto()
  )

  @PostMapping
  fun formularAbsenden(
    @ModelAttribute kontakt: KontaktAnfrageDto,
    model: Model,
    redirectAttributes: RedirectAttributes,
  ): String {
    val ergebnis =
      captchaService.pruefeCaptcha(
        id = kontakt.captchaId,
        antwort = kontakt.captchaAntwort,
      )
    if (ergebnis != CaptchaPruefungErgebnis.Gueltig) {
      return neuesFormular(
        model,
        kontakt,
        fehler = "Captcha ungültig. Bitte erneut versuchen."
      )
    }

    println(
      "\u2709 Neue Nachricht erhalten: ${kontakt.nachricht} " +
        "von ${kontakt.name} <${kontakt.email}>",
    )
    redirectAttributes.addFlashAttribute(
      "erfolg",
      "Nachricht erfolgreich versendet!",
    )
    return "redirect:/kontakt"
  }

  private fun neuesFormular(
    model: Model, kontakt: KontaktAnfrageDto, fehler: String? = null,
  ): String {
    val captchaRaetsel = captchaService.neuesCaptcha()
    model.addAttribute("kontakt", KontaktAnfrageDto())
    model.addAttribute("captchaId", captchaRaetsel.id.toString())
    model.addAttribute("captchaImage", captchaRaetsel.bildDataUrl)
    fehler?.let { model.addAttribute("fehler", it) }
    return "kontakt"
  }
}
