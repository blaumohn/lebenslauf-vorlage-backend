package com.lebenslauf.common.jpa.testsupport

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

@TestConfiguration
class TxConfigRequiresNew {
  @Bean
  fun txTemplateRequiresNew(txManager: PlatformTransactionManager) =
    TransactionTemplate(txManager).apply {
      propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }
}
