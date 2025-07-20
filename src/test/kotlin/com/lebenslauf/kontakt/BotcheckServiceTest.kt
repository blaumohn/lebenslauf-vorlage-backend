package com.lebenslauf.kontakt

import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.nio.file.Paths

class BotcheckServiceTest {

    private val dienst = BotcheckService()

    private fun ladeFixture(name: String): String {
        val pfad = Paths.get("src/test/fixtures/html/$name.html")
        return Files.readString(pfad)
    }

    private val platzhalterId = "thymeleaf-botcheck-kontakt"

    @Test
    fun `captcha wird korrekt ersetzt`() {
        val quellHtml = ladeFixture("formular_mit_platzhalter")
        val captchaHtml = "<img src='/captcha.png' /><input name='botcheck' />"

        val ergebnisHtml = dienst.insertCaptcha(
            quellHtml,
            platzhalterId,
            captchaHtml,
        )

        val doc = Jsoup.parse(ergebnisHtml)
        val aktuellesHtml = doc.getElementById(platzhalterId)?.html()
        val erwartetesHtml = Jsoup.parse(captchaHtml).body().html()

        assertThat(aktuellesHtml)
            .describedAs("Captcha HTML wurde nicht eingefügt.")
            .isEqualTo(erwartetesHtml)

        assertThat(ergebnisHtml)
            .describedAs("Platzhaltertext wurde nicht ersetzt.")
            .doesNotContain("[captcha]")
    }

    @Test
    fun `fehlender platzhalter wirft exception`() {
        val quellHtml = ladeFixture("formular_ohne_platzhalter")
        val captchaHtml = "<img src='/captcha.png' />"

        val fehler = assertThrows<MissingPlaceholderException> {
            dienst.insertCaptcha(quellHtml, platzhalterId, captchaHtml)
        }

        assertThat(fehler.message)
            .describedAs("Fehlermeldung enthält nicht die Platzhalter-ID")
            .contains(platzhalterId)
    }
}
