package com.lebenslauf.kontakt.repository

import com.lebenslauf.common.evalT
import com.lebenslauf.common.jpa.testsupport.JpaServiceMitRequiresNewTx
import com.lebenslauf.common.jpa.testsupport.TxConfigRequiresNew
import com.lebenslauf.kontakt.model.CaptchaSitzung
import com.lebenslauf.kontakt.testsupport.CaptchaSitzungTestBeans
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@DataJpaTest
@Import(TxConfigRequiresNew::class, CaptchaSitzungTestBeans::class)
// @Transactional(propagation = Propagation.NOT_SUPPORTED)
// nutzt application-dev.properties + H2
@ActiveProfiles(*arrayOf("dev", "test"))
class CaptchaSitzungRepositoryTest {

    @Autowired
    lateinit var repository: CaptchaSitzungRepository

    @Autowired
    lateinit var saver: JpaServiceMitRequiresNewTx<
        CaptchaSitzung,
        UUID,
        CaptchaSitzungRepository,
        >

    @Autowired
    lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `gespeicherte Sitzung ist lesbar und korrekt`() {
        val sessionEingabe = CaptchaSitzung(
            captchaText = "TEST123",
        )

        val session = repository.saveAndFlush(sessionEingabe)
        val id = session.id

        val geladen = repository.findById(id.evalT()).orElse(null)
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
            aktiv.id.evalT(),
        )
        val ignoriert = repository.findByIdAndArchiviertFalse(
            archiviert.id.evalT(),
        )

        assertThat(gefunden).isNotNull
        assertThat(ignoriert).isNull()
    }

    @Test
    fun `gespeicherte Sitzung hat ein aktuelles Erzeugungsdatum`() {
        val sessionEingabe = CaptchaSitzung(
            captchaText = "TEST123",
        )

        val eintrag = saver.saveAndReload(sessionEingabe)

        val jetzt = LocalDateTime.now()
        assertThat(eintrag.datum)
            .isCloseTo(jetzt, within(100, ChronoUnit.MILLIS))
    }

    @Test
    fun `alte Sitzungen loeschen funktioniert`() {
        val maxVersuche = 10
        val alt = saver.saveAndReload(CaptchaSitzung(captchaText = "OLD"))
        TestTransaction.flagForCommit()
        TestTransaction.end()
        TestTransaction.start()
        lateinit var neu: CaptchaSitzung

        loop@ for (i in 1..maxVersuche) {
            Thread.sleep(16)
            val istAktiv = TransactionSynchronizationManager
                .isActualTransactionActive()
            neu = saver.saveAndReload(CaptchaSitzung(captchaText = "NEW"))
            TestTransaction.flagForCommit()
            TestTransaction.end()
            println("Transaktion aktiv? $istAktiv")
            TestTransaction.start()
            if (neu.datum.evalT().isAfter(alt.datum)) break@loop // return neu
        }
        require(neu.datum.evalT().isAfter(alt.datum)) {
            "Konnte keinen neueren Timestamp erzeugen – Testumgebung problematisch?"
        }

        val grenze = alt.datum.evalT().plus(1, ChronoUnit.MILLIS)
        val gelöscht =
            repository.deleteAllByDatumBeforeAndArchiviertFalse(grenze)
        assertThat(gelöscht).isEqualTo(1)
    }
}
