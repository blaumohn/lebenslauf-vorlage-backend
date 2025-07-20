package com.lebenslauf.kontakt

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class CaptchaConfig {
    @Bean
    fun kaptchaProducer(): DefaultKaptcha {
        val props = Properties().apply {
            put("kaptcha.textproducer.char.length", "5")
            put(
                "kaptcha.textproducer.char.string",
                "ABCDEFGHJKLMNPQRSTUVWXYZ23456789",
            )
            put(
                "kaptcha.obscurificator.impl",
                "com.google.code.kaptcha.impl.WaterRipple",
            )
            put(
                "kaptcha.noise.impl",
                "com.google.code.kaptcha.impl.DefaultNoise",
            )
            put("kaptcha.image.width", "200")
            put("kaptcha.image.height", "70")
            put("kaptcha.image.border", "no")
        }
        return DefaultKaptcha().apply { config = Config(props) }
    }
}
