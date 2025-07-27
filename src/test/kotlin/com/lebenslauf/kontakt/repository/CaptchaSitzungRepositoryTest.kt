package com.lebenslauf.kontakt.repository

import com.lebenslauf.common.orFail
import com.lebenslauf.kontakt.model.CaptchaSitzung
import com.lebenslauf.testutils.config.TestAuditingConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.*
// import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@DataJpaTest
@Import(TestAuditingConfig::class)
// @EnableJpaAuditing
// nutzt application-dev.properties + H2
@ActiveProfiles(*arrayOf("dev", "test"))
class CaptchaSitzungRepositoryTest {

    @Autowired
    lateinit var repository: CaptchaSitzungRepository

    @BeforeEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `gespeicherte Sitzung ist lesbar und korrekt`() {
        val sessionEingabe = CaptchaSitzung(
            captchaText = "TEST123",
            // datum = LocalDateTime.now(),
        )

        val session = repository.saveAndFlush(sessionEingabe)
        val id = session.id

        val geladen = repository.findById(id.orFail()).orElse(null)
        assertThat(geladen).isNotNull
        assertThat(geladen?.captchaText).isEqualTo("TEST123")
        assertThat(geladen?.archiviert).isFalse()
    }

    @Test
    fun `findByIdAndArchiviertFalse liefert nur nicht-archivierte Sitzungen`() {
        val aktiv = repository.save(
            CaptchaSitzung(captchaText = "A", archiviert = false),
        )
        val archiviert = repository.save(
            CaptchaSitzung(captchaText = "B", archiviert = true),
        )

        val gefunden = repository.findByIdAndArchiviertFalse(
            aktiv.id.orFail(),
        )
        val ignoriert = repository.findByIdAndArchiviertFalse(
            archiviert.id.orFail(),
        )

        assertThat(gefunden).isNotNull
        assertThat(ignoriert).isNull()
    }
//    @Test
//    fun `alte Sitzungen loeschen funktioniert`() {
//        val alt = repository.save(
//            CaptchaSitzung(
//                captchaText = "OLD",
//                datum = LocalDateTime.now().minusMinutes(40),
//            ),
//        )
//        val neu = repository.save(
//            CaptchaSitzung(
//                captchaText = "NEW",
//                datum = LocalDateTime.now(),
//            ),
//        )
//
//        val anzahl = repository.deleteAllByDatumBeforeAndArchiviertFalse(
//            LocalDateTime.now().minusMinutes(30),
//        )
//
//        val uebrig = repository.findAll()
//        assertThat(anzahl).isEqualTo(1)
//        assertThat(uebrig).extracting("captchaText").containsExactly("NEW")
//    }

//    @Test
//    fun `null captchaText wirft PersistenceException`() {
//        val fehlerhafteSitzung = CaptchaSitzung(
//            captchaText = null as String,// absichtlich falsch
//            datum = LocalDateTime.now(),
//        )
//
//        assertThrows<PersistenceException> {
//            repository.saveAndFlush(fehlerhafteSitzung)
//        }
//    }
}
