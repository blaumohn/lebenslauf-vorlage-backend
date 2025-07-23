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
class KontaktController(
    private val captchaService: CaptchaTokenService,
) {

    @GetMapping
    fun formularAnzeigen(model: Model): String {
        val captcha = captchaService.neuesCaptcha()
        model.addAttribute("kontakt", KontaktAnfrageDto())
        model.addAttribute("captchaToken", captcha.token)
        model.addAttribute("captchaImage", captcha.bildBase64)
        return "kontakt"
    }

    @PostMapping
    fun formularAbsenden(
        @ModelAttribute kontakt: KontaktAnfrageDto,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val ergebnis = captchaService.pruefeToken(
            kontakt.captchaToken,
            kontakt.captchaAntwort,
        )
        if (ergebnis != CaptchaPruefungErgebnis.Gueltig) {
            val neuesCaptcha = captchaService.neuesCaptcha()
            model.addAttribute(
                "fehler",
                "Captcha ungültig. Bitte erneut versuchen.",
            )
            model.addAttribute("kontakt", kontakt)
            model.addAttribute("captchaToken", neuesCaptcha.token)
            model.addAttribute("captchaImage", neuesCaptcha.bildBase64)
            return "kontakt"
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
}
