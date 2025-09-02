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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@DataJpaTest
@Import(
  TxConfigRequiresNew::class,
  CaptchaSitzungTestBeans::class,
  PostgresTestConfig::class,
)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class CaptchaSitzungRepositoryTest {
  @Autowired lateinit var repository: CaptchaSitzungRepository

  @Autowired
  lateinit var transactionHelfer: JpaServiceMitRequiresNewTx<
    CaptchaSitzung,
    UUID,
    CaptchaSitzungRepository,
  >

  @Autowired lateinit var entityManager: EntityManager

  @BeforeEach
  fun setUp() {
    repository.deleteAll()
  }

  @Test
  fun `gespeicherte Sitzung ist lesbar und korrekt`() {
    val sessionEingabe =
      CaptchaSitzung(
        captchaText = "TEST123",
      )

    val session = repository.saveAndFlush(sessionEingabe)
    val id = session.id.evalT()

    val geladen = repository.findById(id).orElse(null)
    assertThat(geladen).isNotNull
    assertThat(geladen?.captchaText).isEqualTo("TEST123")
    assertThat(geladen?.archiviert).isFalse()
  }

  @DisplayName(
    "findByIdAndArchiviertFalse liefert nur nicht-archivierte Sitzungen",
  )
  @Test
  fun findByIdAndArchiviertFalse1() {
    val aktiv =
      repository.save(
        CaptchaSitzung(captchaText = "A", archiviert = false),
      )
    val archiviert =
      repository.save(
        CaptchaSitzung(captchaText = "B", archiviert = true),
      )

    val gefunden =
      repository.findByIdAndArchiviertFalse(
        aktiv.id.evalT(),
      )
    val ignoriert =
      repository.findByIdAndArchiviertFalse(
        archiviert.id.evalT(),
      )

    assertThat(gefunden).isNotNull
    assertThat(ignoriert).isNull()
  }

  @Test
  fun `gespeicherte Sitzung hat ein aktuelles Erzeugungsdatum`() {
    val sessionEingabe = CaptchaSitzung(captchaText = "TEST123")

    val eintrag = repository.saveAndFlush(sessionEingabe)

    val epochMillis =
      (
        entityManager
          .createNativeQuery(
            "select (extract(epoch from clock_timestamp())*1000)::bigint",
          ).singleResult as Number
      ).toLong()

    val dbInstant = Instant.ofEpochMilli(epochMillis)

    assertThat(eintrag.datum).isNotNull
    assertThat(eintrag.datum!!.toInstant())
      .isCloseTo(
        dbInstant,
        within(200, ChronoUnit.MILLIS),
      )
  }

  @Test
  fun `alte Sitzungen loeschen funktioniert`() {
    val maxVersuche = 10
    val alt =
      transactionHelfer.saveAndFlush(
        CaptchaSitzung(captchaText = "OLD"),
      )
    lateinit var neu: CaptchaSitzung

    loop@ for (i in 1..maxVersuche) {
      Thread.sleep(16)
      neu =
        transactionHelfer.saveAndFlush(
          CaptchaSitzung(captchaText = "NEW"),
        )
      if (neu.datum.evalT().isAfter(alt.datum)) break@loop
    }
    require(neu.datum.evalT().isAfter(alt.datum)) {
      "Konnte keinen neueren Timestamp erzeugen – Testumgebung problematisch?"
    }

    val grenze = alt.datum.evalT().plus(1, ChronoUnit.MILLIS)
    val gelöscht =
      repository.deleteAllByDatumBeforeAndArchiviertFalse(
        grenze,
      )
    assertThat(gelöscht).isEqualTo(1)
  }

  @Test
  fun `datum bleibt unverändert bei merge`() {
    val s1 = CaptchaSitzung(captchaText = "X")
    entityManager.persist(s1)
    entityManager.flush()

    val id = s1.id.evalT()
    val vorher = s1.datum.evalT()

    val ver = s1.copy(datum = vorher.plusDays(2)) // detached Kopie
    val managed = entityManager.merge(ver)
    entityManager.flush()

    entityManager.clear()
    val neu = entityManager.find(CaptchaSitzung::class.java, id)
    assertThat(neu.datum).isEqualTo(vorher)
  }
}
