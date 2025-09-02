package com.lebenslauf.common.jpa

import com.lebenslauf.common.evalT
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.JpaRepository

interface HasId<ID> {
  val id: ID?
}

/**
 * Generischer technischer JPA-Service, der ein Repository kapselt,
 * um z. B. `saveAndReload` transaktional auszuführen.
 */
class JpaService<T : HasId<ID>, ID : Any, R : JpaRepository<T, ID>>(
  val repository: R,
  private val entityManager: EntityManager,
) {
  // @Transactional(propagation = Propagation.REQUIRES_NEW)
  fun saveAndReload(entity: T): T {
    val saved = repository.saveAndFlush(entity)
    // entityManager.refresh(saved)
    return repository.findById(saved.id.evalT()).evalT()
  }

  // Optional: Zugriff auf Repo oder Erweiterung möglich
  fun deleteById(id: ID) = repository.deleteById(id)

  fun findById(id: ID): T = repository.findById(id).evalT()
}
