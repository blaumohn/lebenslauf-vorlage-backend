package com.lebenslauf.kontakt

data class CaptchaToken(
    val hash: String,
    val timestamp: Long,
    val signature: String,
) {
    companion object {
        fun parse(raw: String): CaptchaToken {
            val teile = raw.split("|")
            require(teile.size == 3) { "Ungültiges Token-Format" }

            val timestamp = teile[1].toLongOrNull()
                ?: throw IllegalArgumentException("Zeitstempel ungültig")

            return CaptchaToken(
                hash = teile[0],
                timestamp = timestamp,
                signature = teile[2],
            )
        }
    }

    fun toRaw(): String = "$hash|$timestamp|$signature"
}
