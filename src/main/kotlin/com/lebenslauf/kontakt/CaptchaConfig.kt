package com.lebenslauf.kontakt

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class CaptchaConfig(
    @Value("classpath:kaptcha.properties")
    private val resource: org.springframework.core.io.Resource,
) {

    @Bean
    fun kaptchaProducer(): DefaultKaptcha {
        val props = Properties().apply {
            resource.inputStream.use { load(it) }
        }
        return DefaultKaptcha().apply { config = Config(props) }
    }
}
