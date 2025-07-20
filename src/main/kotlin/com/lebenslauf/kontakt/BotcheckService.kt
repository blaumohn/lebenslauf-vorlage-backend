package com.lebenslauf.kontakt

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

class MissingPlaceholderException(message: String) : RuntimeException(message)

@Service
class BotcheckService {
    private val log = LoggerFactory.getLogger(BotcheckService::class.java)

    /**
     * Ersetzt ein HTML-Element mit einer bestimmten ID durch bereitgestelltes CAPTCHA-HTML.
     * Falls kein passendes Element gefunden wird, wird eine Exception geworfen.
     */
    fun insertCaptcha(
        html: String,
        placeholderId: String,
        captchaHtml: String,
    ): String {
        val doc: Document = Jsoup.parse(html)

        val placeholder: Element? = doc.getElementById(placeholderId)
        if (placeholder != null) {
            placeholder.html(captchaHtml)
            return doc.outerHtml()
        }

        log.error(
            "Botcheck-Platzhalter mit ID '$placeholderId' nicht gefunden – " +
                "Build sollte korrigiert werden.",
        )
        throw MissingPlaceholderException(
            "Platzhalter-ID '$placeholderId' nicht im HTML gefunden",
        )
    }
}
