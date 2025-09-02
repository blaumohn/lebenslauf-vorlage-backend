package com.lebenslauf.kontakt.model

import com.lebenslauf.common.jpa.HasId
import io.hypersistence.utils.hibernate.type.basic.Inet
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLInetType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.hibernate.annotations.Type
import org.hibernate.generator.EventType
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@DynamicInsert
@Table(name = "captcha_sitzung", schema = "public")
data class CaptchaSitzung(
  @Id
  @Column(name = "id", columnDefinition = "uuid")
  @Generated(event = [EventType.INSERT])
  override var id: UUID? = null,

  @Column(
    name = "captcha_text",
    nullable = false,
    length = 255,
  ) val captchaText: String,

  @Column(
    name = "datum",
    columnDefinition = "timestamptz",
    insertable = false,
    updatable = false,
  )
  @Generated(event = [EventType.INSERT])
  val datum: OffsetDateTime? = null,

  @Column(
    name = "archiviert",
    nullable = false,
  ) val archiviert: Boolean = false,

  @Type(PostgreSQLInetType::class)
  @Column(name = "ip_address", columnDefinition = "inet")
  val ipAddress: Inet? = null,

  @Column(
    name = "user_agent",
    columnDefinition = "text",
  ) val userAgent: String? =
    null,

  @Column(name = "status", nullable = false, length = 16)
  val status: String = "NEU",

  @Column(name = "verbraucht_am", columnDefinition = "timestamptz")
  val verbrauchtAm: OffsetDateTime? = null,

  @Column(
    name = "request_count",
    nullable = false,
  ) val requestCount: Int = 0,
) : HasId<UUID>
