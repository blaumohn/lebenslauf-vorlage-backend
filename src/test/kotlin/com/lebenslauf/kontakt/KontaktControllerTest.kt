package com.lebenslauf.kontakt
import com.fasterxml.jackson.databind.ObjectMapper
import com.lebenslauf.testutils.TestFixtures.createKontaktAnfrageDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(KontaktController::class)
class KontaktControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var objectMapper: ObjectMapper

    @Test
    fun `gueltige Anfrage wird akzeptiert`() {
        var dto = createKontaktAnfrageDto()
        mockMvc
            .post("/api/kontakt") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
            }
            .andExpect { status { isOk() } }
    }

    @Test
    fun `ungueltiger Botcheck wird abgelehnt`() {
        val dto = createKontaktAnfrageDto(botcheck = "falsch")

        mockMvc
            .post("/api/kontakt") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(dto)
            }
            .andExpect { status { isBadRequest() } }
    }
}
