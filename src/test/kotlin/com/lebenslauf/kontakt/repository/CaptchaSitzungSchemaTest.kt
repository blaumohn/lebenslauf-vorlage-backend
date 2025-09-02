package com.lebenslauf.kontakt.repository

import com.lebenslauf.common.evalT
import com.lebenslauf.kontakt.model.CaptchaSitzung
import io.hypersistence.utils.hibernate.type.basic.Inet
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@Import(PostgresTestConfig::class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class CaptchaSitzungSchemaTest {
  @Autowired lateinit var entityManager: EntityManager

  @Autowired lateinit var repository: CaptchaSitzungRepository

  @BeforeEach
  fun clean() {
    repository.deleteAll()
    entityManager.flush()
    entityManager.clear()
  }

  // ---------- Hilfen ----------

  private fun explain(sqlAnfrage: String): String {
    entityManager
      .createNativeQuery(
        "set enable_seqscan=off",
      ).executeUpdate()
    @Suppress("UNCHECKED_CAST")
    val rows =
      entityManager
        .createNativeQuery("explain $sqlAnfrage")
        .resultList as List<Any?>
    return rows.joinToString("\n") { it.toString() }
  }

  private data class Ix(val name: String, val predicate: String?)

  private fun indizes(): List<Ix> {
    @Suppress("UNCHECKED_CAST")
    val rows =
      entityManager
        .createNativeQuery(
          """
          select ci.relname as indexname,
                 pg_get_expr(ix.indpred, ix.indrelid) as predicate
          from pg_index ix
          join pg_class ci on ci.oid = ix.indexrelid
          join pg_class ct on ct.oid = ix.indrelid
          join pg_namespace ns on ns.oid = ct.relnamespace
          where ns.nspname='public' and ct.relname='captcha_sitzung'
          """.trimIndent(),
        ).resultList as List<Array<Any?>>
    return rows.map { Ix(it[0] as String, it[1] as String?) }
  }

  // ---------- DEFAULT / NOT NULL / CHECK ----------

  @Test
  fun `DEFAULT-Werte und NOT-NULL greifen`() {
    val s = repository.saveAndFlush(CaptchaSitzung(captchaText = "DEF"))
    val r = repository.findById(s.id.evalT()).evalT()

    assertThat(r.archiviert).isFalse() // DEFAULT false
    assertThat(r.status).isEqualTo("NEU") // DEFAULT 'NEU'
    assertThat(r.requestCount).isEqualTo(0) // DEFAULT 0
    assertThat(r.datum).isNotNull // DEFAULT now()
  }

  @Test
  fun `CHECK verhindert negative request_count`() {
    val sqlAnfrage =
      """
      insert into public.captcha_sitzung (captcha_text, request_count)
      values ('X', -1)
      """
    org.junit.jupiter.api
      .assertThrows<ConstraintViolationException> {
        entityManager
          .createNativeQuery(sqlAnfrage)
          .executeUpdate()
        entityManager.flush()
      }
  }

  @Test
  fun `NOT NULL auf captcha_text wird erzwungen`() {
    org.junit.jupiter.api
      .assertThrows<jakarta.persistence.PersistenceException> {
        entityManager
          .createNativeQuery(
            "insert into public.captcha_sitzung (captcha_text) values (null)",
          ).executeUpdate()
        entityManager.flush()
      }
  }

  // ---------- Typen + Extension ----------

  @Test
  fun `Spaltentypen und DEFAULT-Ausdruecke wie definiert`() {
    @Suppress("UNCHECKED_CAST")
    val rows =
      entityManager
        .createNativeQuery(
          """
          select column_name, data_type, udt_name, is_nullable, coalesce(column_default, '')
          from information_schema.columns
          where table_schema='public' and table_name='captcha_sitzung'
          """.trimIndent(),
        ).resultList as List<Array<Any?>>

    fun row(n: String) = rows.first { (it[0] as String) == n }

    assertThat(row("id")[2]).isEqualTo("uuid")
    assertThat(row("captcha_text")[1]).isEqualTo("character varying")
    assertThat(row("datum")[2]).isEqualTo("timestamptz")
    assertThat(row("archiviert")[1]).isEqualTo("boolean")
    assertThat(row("ip_address")[2]).isEqualTo("inet")
    assertThat(row("status")[1]).isEqualTo("character varying")
    assertThat(row("request_count")[1]).isEqualTo("integer")

    assertThat(row("id")[4] as String).contains("gen_random_uuid()")
    assertThat(row("datum")[4] as String).contains("now()")
  }

  @Test
  fun `Extension pgcrypto ist installiert`() {
    val ok =
      entityManager
        .createNativeQuery(
          "select 1 from pg_extension where extname='pgcrypto'",
        ).resultList
        .isNotEmpty()
    assertThat(ok).isTrue()
  }

  // ---------- Indizes vorhanden + Prädikat korrekt ----------

  @Test
  fun `Indizes existieren und Partial-Praedikat stimmt`() {
    val ixs = indizes()
    assertThat(
      ixs.any { it.name == "idx_captcha_sitzung_archiviert" },
    ).isTrue()
    assertThat(ixs.any { it.name == "idx_captcha_sitzung_ip" }).isTrue()

    val offen =
      ixs
        .firstOrNull {
          it.name == "idx_captcha_sitzung_offen"
        }.evalT()
    assertThat(offen.predicate).isEqualTo("(archiviert = false)")
  }

  // ---------- Index-Nutzung (EXPLAIN) ----------

  @Test
  @DisplayName("Index auf archiviert wird genutzt (Index Scan)")
  fun indexAufArchiviertWirdGenutzt() {
    for (i in 1..80) {
      repository.save(
        CaptchaSitzung(
          captchaText = "X$i",
          archiviert = (
            i %
              2 ==
              0
          ),
        ),
      )
    }
    repository.flush()

    val plan =
      explain(
        "select * from public.captcha_sitzung where archiviert = false",
      )
    assertThat(plan).containsIgnoringCase("Index Scan")
    assertThat(plan).contains("idx_captcha_sitzung_archiviert")
  }

  @Test
  @DisplayName(
    "Partial-Index idx_captcha_sitzung_offen greift bei archiviert=false und status='NEU'",
  )
  fun partialIndexWirdGenutzt() {
    for (i in 1..120) {
      repository.save(
        CaptchaSitzung(
          captchaText = "S$i",
          archiviert = (i % 3 == 0),
          status = if (i % 2 == 0) "NEU" else "GELÖST",
        ),
      )
    }
    repository.flush()

    val plan =
      explain(
        "select * from public.captcha_sitzung where archiviert=false and status='NEU'",
      )
    assertThat(plan).contains("Index Scan")
    assertThat(plan).contains("idx_captcha_sitzung_offen")
  }

  @Test
  fun `Index auf ip_address wird genutzt`() {
    for (i in 1..50) {
      repository.save(
        CaptchaSitzung(
          captchaText = "I$i",
          ipAddress = Inet("127.0.0.${i % 4 + 1}"),
        ),
      )
    }
    repository.flush()

    val plan =
      explain(
        "select * from public.captcha_sitzung where ip_address = '127.0.0.2'",
      )
    assertThat(plan).contains("Index Scan")
    assertThat(plan).contains("idx_captcha_sitzung_ip")
  }
}
