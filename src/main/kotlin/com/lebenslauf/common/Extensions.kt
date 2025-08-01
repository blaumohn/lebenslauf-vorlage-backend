package com.lebenslauf.common

import java.util.Optional

fun <T : Any> T?.evalT(
    message: String = "evalT: Wert darf nicht null sein",
): T = this ?: error(message)

fun <T : Any> Optional<T>.evalT(
    message: String = "evalT: Wert darf nicht leer sein",
): T {
    return this.orElseThrow { IllegalStateException(message) }
}
