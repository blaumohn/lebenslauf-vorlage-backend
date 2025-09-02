package com.lebenslauf.testutils

import com.google.code.kaptcha.impl.DefaultKaptcha
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

object TestCaptchas {
  fun platzhalterBild(): BufferedImage {
    val img = BufferedImage(200, 70, BufferedImage.TYPE_INT_RGB)
    val g: Graphics2D = img.createGraphics()
    g.color = Color.LIGHT_GRAY
    g.fillRect(0, 0, 200, 70)
    g.color = Color.BLACK
    g.drawString("TEST CAPTCHA", 40, 40)
    g.dispose()
    return img
  }
}

class TestKaptcha : DefaultKaptcha() {
  var letzterText: String? = null

  override fun createText(): String {
    val text = super.createText()
    letzterText = text
    return text
  }
}
