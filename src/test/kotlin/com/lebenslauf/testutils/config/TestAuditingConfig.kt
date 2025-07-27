package com.lebenslauf.testutils.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.AuditorAware
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

@TestConfiguration
//@EnableJpaAuditing
class TestAuditingConfig {
    @Bean
    fun auditorProvider(): AuditorAware<String> =
        AuditorAware { Optional.of("test-user") }
}
