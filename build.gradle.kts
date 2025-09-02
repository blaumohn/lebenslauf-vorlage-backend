import org.springframework.boot.gradle.tasks.run.BootRun
import java.io.FileInputStream
import java.util.Properties

plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  kotlin("plugin.jpa") version "1.9.25"
  id("org.springframework.boot") version "3.5.3"
  id("io.spring.dependency-management") version "1.1.7"
  id("com.diffplug.spotless") version "6.25.0"
}

group = "com.lebenslauf"

version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

repositories { mavenCentral() }

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.jsoup:jsoup:1.17.1")
  implementation("com.github.penggle:kaptcha:2.3.2")
  implementation("org.liquibase:liquibase-core:4.28.0")

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation(
    "io.hypersistence:hypersistence-utils-hibernate-63:3.9.2",
  )
  runtimeOnly("org.postgresql:postgresql:42.7.3")

  testImplementation("org.assertj:assertj-core:3.24.2")
  testImplementation(
    "org.springframework.boot:spring-boot-starter-test",
  ) {
    exclude(
      group = "org.junit.vintage",
      module = "junit-vintage-engine",
    )
  }
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  testImplementation(
    "org.springframework.boot:spring-boot-testcontainers",
  )
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:postgresql")
}

kotlin {
  compilerOptions { freeCompilerArgs.addAll(listOf("-Xjsr305=strict")) }
}

tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

tasks.test {
  useJUnitPlatform()
  reports.html.required.set(true)
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
  environment.putAll(ladeProperties(".env.properties"))
}

tasks.test {
  environment.putAll(ladeProperties(".env.properties"))
  testLogging { showStandardStreams = true }
}

spotless {
  kotlin {
    target("src/**/*.kt")
    ktlint("1.2.1").editorConfigOverride(
      mapOf("max_line_length" to "80"),
    )
  }
}

fun ladeProperties(pfad: String = ".env.properties"): Map<String, String> {
  val props = Properties()
  FileInputStream(pfad).use { props.load(it) }
  return props.entries.associate { (k, v) ->
    k.toString() to v.toString()
  }
}
