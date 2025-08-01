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
    fun saveAndReload(entity: T): T = txTemplate.execute<T> {
        val saved = repository.saveAndFlush(entity)
        // WICHTIG: explizit den Wert an execute zurückgeben
        return@execute repository.findById(saved.id.evalT()).evalT()
    } ?: error("TransactionTemplate lieferte null")
}
