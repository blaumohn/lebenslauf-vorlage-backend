package com.lebenslauf.kontakt

data class KontaktAnfrageDto(
    var name: String = "",
    var email: String = "",
    var nachricht: String = "",
    var captchaAntwort: String = "",
    var captchaToken: String = "",
)
