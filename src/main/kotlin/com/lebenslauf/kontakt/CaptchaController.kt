package com.lebenslauf.kontakt
import com.google.code.kaptcha.impl.DefaultKaptcha
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@RestController
class CaptchaController(private val kaptchaProducer: DefaultKaptcha) {
  @GetMapping("/captcha", produces = [MediaType.IMAGE_PNG_VALUE])
  fun getCaptcha(session: HttpSession, response: HttpServletResponse) {
    val capText = kaptchaProducer.createText()
    session.setAttribute("captcha", capText)
    val image = kaptchaProducer.createImage(capText)
    response.outputStream.use { ImageIO.write(image, "png", it) }
  }
}
