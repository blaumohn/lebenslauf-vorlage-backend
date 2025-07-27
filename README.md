
## 🔚 Aktueller Stand und Zweck dieses Branches

Da dieses Projekt sich momentan in einer frühen Phase befindet und das Auditing noch keinen funktionalen Nutzen bringt, wurde entschieden, Auditing vorerst zurückzustellen (YAGNI-Prinzip).

Diese Datei dokumentiert den Stand des Branches jpa-auditing, der für spätere Referenz erhalten bleibt.

- Die Integration von JPA Auditing wurde begonnen, aber nicht vollständig abgeschlossen

- Die CaptchaSitzungRepositoryTest wurde bewusst ohne vollständige Auditing-Konfiguration stabilisiert

- Dieser Branch dient der Erinnerung und Vorbereitung für eine mögliche spätere Wiederaufnahme

## 📌Branch-Überblick

- In diesem Branch wird eine neue Model-Klasse sowie ein Test für die Captcha-Sitzungstabelle eingeführt.
- Die Captcha-Sitzung wird in H2 gespeichert und nach einer gewissen Zeit gelöscht.
- Die Lösung funktioniert, das Kontaktformular läuft, aber einige Tests schlagen fehl, da die für JPA Auditing erforderliche Konfiguration (z. B. jpaAuditingHandler) noch nicht vollständig eingerichtet ist.
- Die Integration von JPA Auditing wurde bewusst auf später verschoben, da sie im Moment als **„premature optimization“** erscheint:
  - Zusätzliche Klassen und Einstellungen für jeden Test
  - Zeitaufwand: Einrichtung des JPA Auditing in Tests ist aktuell noch nicht abgeschlossen und würde zusätzlichen Zeitaufwand bedeuten; auch zukünftige Tests müssten ggf. angepasst werden
  - In diesem Anwendungsfall genügt oft ein einfacher SQL-Default

---

# JPA Auditing Fehler und Lösungsansätze

## ❗ Fehlerbeschreibung

Bei Einführung von JPA Auditing (`@EnableJpaAuditing`) im Spring Boot Projekt traten folgende Probleme auf:

- ❌ **`BeanCreationException` bei `jpaAuditingHandler`** in `@WebMvcTest`:
  > `Cannot resolve reference to bean 'jpaMappingContext'`

- ❌ **Konflikte bei paralleler Aktivierung in mehreren Tests**:
  > `The bean 'jpaAuditingHandler' could not be registered. A bean with that name has already been defined...`

Diese traten auf, sobald `@EnableJpaAuditing` oder `TestAuditingConfig` testübergreifend aktiviert wurden.

---

## ✅ Empfohlene Zerlegung

### 1. Zwei dedizierte Testkonfigurationen einführen

#### a) Für JPA-Tests (z. B. `@DataJpaTest`):

```kotlin
@Configuration
@EnableJpaAuditing
@EntityScan("com.lebenslauf")
@EnableJpaRepositories("com.lebenslauf")
class TestJpaAuditingConfig
```

Importieren in Tests:

```kotlin
@DataJpaTest
@Import(TestJpaAuditingConfig::class)
```

#### b) Für MVC-Tests (z. B. `@WebMvcTest`):

```kotlin
@Configuration
class MockJpaAuditingConfig {
    @Bean
    fun dateTimeProvider(): DateTimeProvider = DateTimeProvider {{ Optional.of(OffsetDateTime.now()) }}
}
```

Optionaler Import in Tests:

```kotlin
@WebMvcTest
@Import(MockJpaAuditingConfig::class)
```

---

### 2. Optional: Profile nutzen

Die produktive `JpaAuditingConfig` kann mit einem Profil versehen werden:

```kotlin
@Configuration
@EnableJpaAuditing
@Profile("auditing")
class JpaAuditingConfig
```

Dann aktiviert man das Feature gezielt im Test:

```kotlin
@ActiveProfiles("test", "auditing")
```

---

## 🛠 Weitere Hinweise

- Vermeide `@EnableJpaAuditing` global, wenn nicht alle Tests es benötigen.
- Stattdessen gezielte Aktivierung per `@Import` oder `@Profile`.
- In kleinen Projekten mit begrenztem Nutzen kann Auditing ggf. später wieder aktiviert werden.
