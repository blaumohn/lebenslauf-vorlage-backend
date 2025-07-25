package com.lebenslauf.kontakt.repository

import com.lebenslauf.kontakt.model.Dummy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyRepository : JpaRepository<Dummy, String>
