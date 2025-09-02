package com.lebenslauf.common

import java.util.Optional

fun <T : Any> T?.evalT(
  message: String = "evalT: Wert darf nicht null sein",
): T = this ?: throw IllegalArgumentException(message)

fun <T : Any> Optional<T>.evalT(
  message: String = "evalT: Wert darf nicht leer sein",
): T = this.orElseThrow { IllegalArgumentException(message) }
