package com.lebenslauf.kontakt.repository

import com.lebenslauf.kontakt.model.CaptchaSitzung
import com.lebenslauf.kontakt.model.CaptchaStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CaptchaSitzungRepository : JpaRepository<CaptchaSitzung, UUID> {
  fun findByIdAndArchiviertFalse(id: UUID): CaptchaSitzung?

  fun deleteAllByErstelltAmBeforeAndArchiviertFalse(
    grenze: OffsetDateTime,
  ): Long

  @Modifying
  @Query("""
    update CaptchaSitzung s
       set s.requestCount = s.requestCount + 1
     where s.id = :id
  """)
  fun incrementRequestCount(@Param("id") id: UUID): Int

  @Modifying
  @Query("""
    update CaptchaSitzung s
       set s.status = :zielStatus,
           s.verbrauchtAm = CURRENT_TIMESTAMP
     where s.id = :id and s.status = :erwartet
  """)
  fun updateStatus(
    @Param("id") id: UUID,
    @Param("zielStatus") zielStatus: CaptchaStatus,
    @Param("erwartet") erwartet: CaptchaStatus
  ): Int
}
