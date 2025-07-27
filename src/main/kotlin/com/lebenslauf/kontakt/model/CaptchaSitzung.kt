package com.lebenslauf.kontakt.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    indexes = [
        Index(name = "idx_id_archiviert", columnList = "id,archiviert"),
        Index(name = "idx_ip", columnList = "ipAddress"),
    ],
)
data class CaptchaSitzung(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    val captchaText: String,

    @CreatedDate
    @Column(updatable = false)
    val datum: LocalDateTime? = null,

    // Für spätere Erweiterung / Filterung:
    val archiviert: Boolean = false,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val status: String = "NEU",
    val verbrauchtAm: LocalDateTime? = null,
    val requestCount: Int = 0,
)
