package com.lebenslauf.common

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

fun BufferedImage.toBase64DataUrl(): String {
  val byteArrayOutputStream = ByteArrayOutputStream()
  ImageIO.write(this, "png", byteArrayOutputStream)
  val byteArray = byteArrayOutputStream.toByteArray()
  val base64 = Base64.getEncoder().encodeToString(byteArray)

  return "data:image/png;base64,$base64"
}
