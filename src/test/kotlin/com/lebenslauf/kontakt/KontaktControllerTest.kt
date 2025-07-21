package com.lebenslauf.kontakt

import com.lebenslauf.testutils.TestCaptchas
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(KontaktController::class)
class KontaktControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var captchaService: CaptchaTokenService

    @Test
    fun `GET kontakt zeigt formular mit captcha`() {
        val mockCaptcha = CaptchaTokenService.CaptchaPaket(
            bild = TestCaptchas.platzhalterBild(),
            tokenFeld = "TEST123|123456789|SIGNATUR",
        )
        given(captchaService.neuesCaptcha()).willReturn(mockCaptcha)

        mockMvc.get("/kontakt")
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
                content { string(containsString("captchaToken")) }
            }
    }

    @Test
    fun `POST kontakt mit ungueltigem captcha zeigt fehlermeldung`() {
        given(captchaService.pruefeToken("FAKE|1|SIG", "falsch"))
            .willReturn(false)

        val form = mapOf(
            "name" to "Max Mustermann",
            "email" to "max@example.com",
            "nachricht" to "Testnachricht",
            "captchaAntwort" to "falsch",
            "captchaToken" to "FAKE|1|SIG",
        )

        val neuerCaptcha = CaptchaTokenService.CaptchaPaket(
            bild = TestCaptchas.platzhalterBild(),
            tokenFeld = "NEU|2|SIGNEU",
        )
        given(captchaService.neuesCaptcha()).willReturn(neuerCaptcha)

        mockMvc.post("/kontakt") {
            param("name", form["name"]!!)
            param("email", form["email"]!!)
            param("nachricht", form["nachricht"]!!)
            param("captchaAntwort", form["captchaAntwort"]!!)
            param("captchaToken", form["captchaToken"]!!)
        }.andExpect {
            status { isOk() }
            content { string(containsString("Captcha ungültig")) }
        }
    }
}
