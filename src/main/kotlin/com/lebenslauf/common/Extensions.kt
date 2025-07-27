package com.lebenslauf.common

inline fun <reified T : Any> T?.orFail(
    message: String = "Wert darf nicht null sein",
): T {
    return this ?: error(message)
}
