package com.lebenslauf.kontakt.testsupport

import com.lebenslauf.common.jpa.testsupport.JpaServiceMitRequiresNewTx
import com.lebenslauf.kontakt.model.CaptchaSitzung
import com.lebenslauf.kontakt.repository.CaptchaSitzungRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.transaction.support.TransactionTemplate
import java.util.*

@TestConfiguration
class CaptchaSitzungTestBeans {

    @Bean
    fun captchaSitzungSaver(
        repository: CaptchaSitzungRepository,
        txTemplateRequiresNew: TransactionTemplate,
    ): JpaServiceMitRequiresNewTx<
        CaptchaSitzung,
        UUID,
        CaptchaSitzungRepository,
        > =
        JpaServiceMitRequiresNewTx(repository, txTemplateRequiresNew)
}
