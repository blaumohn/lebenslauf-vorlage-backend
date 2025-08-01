package com.lebenslauf.kontakt.model

import com.lebenslauf.common.jpa.HasId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.Generated
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    indexes = [
        Index(name = "idx_id_archiviert", columnList = "id,archiviert"),
        Index(name = "idx_ip", columnList = "ipAddress"),
    ],
)
data class CaptchaSitzung(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override val id: UUID? = null,
    val captchaText: String,

    @Column(insertable = false, updatable = false)
    @Generated
    val datum: LocalDateTime? = null,

    // Für spätere Erweiterung / Filterung:
    val archiviert: Boolean = false,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val status: String = "NEU",
    val verbrauchtAm: LocalDateTime? = null,
    val requestCount: Int = 0,
) : HasId<UUID>
