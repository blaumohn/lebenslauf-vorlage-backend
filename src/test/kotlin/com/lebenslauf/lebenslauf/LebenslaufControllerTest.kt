package com.lebenslauf.lebenslauf

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class LebenslaufControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `ohne Token gibt öffentliche Fassung`() {
        mockMvc.get("/api/lebenslauf").andExpect {
            status { isOk() }
            content { string("öffentlich") }
        }
    }

    @Test
    fun `gültiger Token ergibt vollständige Fassung`() {
        mockMvc.get("/api/lebenslauf?token=abc123").andExpect {
            status { isOk() }
            content { string("vollständig") }
        }
    }

    @Test
    fun `ungültiger Token ergibt öffentliche Fassung`() {
        mockMvc.get("/api/lebenslauf?token=foobar").andExpect {
            status { isOk() }
            content { string("öffentlich") }
        }
    }
}
