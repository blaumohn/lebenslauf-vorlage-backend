package com.lebenslauf.common.jpa.testsupport

import com.lebenslauf.common.evalT
import com.lebenslauf.common.jpa.HasId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.support.TransactionTemplate

class JpaServiceMitRequiresNewTx<
  T : HasId<ID>,
  ID : Any,
  R : JpaRepository<T, ID>,
>(
  private val repository: R,
  private val txTemplate: TransactionTemplate,
) {
  fun saveAndFlush(entity: T): T =
    txTemplate.execute<T> {
      return@execute repository.saveAndFlush(entity)
    } ?: error("TransactionTemplate lieferte null")
}
