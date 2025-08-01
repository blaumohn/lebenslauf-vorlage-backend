package com.lebenslauf.kontakt.repository

import com.lebenslauf.kontakt.model.CaptchaSitzung
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.UUID

interface CaptchaSitzungRepository : JpaRepository<CaptchaSitzung, UUID> {
    fun findByIdAndArchiviertFalse(id: UUID): CaptchaSitzung?
    fun deleteAllByDatumBeforeAndArchiviertFalse(grenze: LocalDateTime): Long
}
