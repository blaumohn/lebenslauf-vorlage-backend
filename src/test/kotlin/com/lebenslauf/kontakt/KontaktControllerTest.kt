package com.lebenslauf.kontakt

import com.lebenslauf.kontakt.CaptchaTokenService.CaptchaPacket
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
        val mockCaptcha =
            CaptchaPacket(
                "TEST|1234567890|SIG",
                "data:image/png;base64;TESTBILD",
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
            .willReturn(CaptchaPruefungErgebnis.Ungueltig)

        val form = mapOf(
            "name" to "Max Mustermann",
            "email" to "max@example.com",
            "nachricht" to "Testnachricht",
            "captchaAntwort" to "falsch",
            "captchaToken" to "FAKE|1|SIG",
        )

        val neuerCaptcha = CaptchaPacket(
            "NEU|1234567890|SIGNEU",
            "data:image/png;base64;TESTBILD1",
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
