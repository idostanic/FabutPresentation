package eu.execom.FabutPresentation.service

import java.security.MessageDigest

import sun.misc.BASE64Encoder

class PasswordEncoder {

  lazy val md = MessageDigest.getInstance("SHA-256")
  lazy val ha = new BASE64Encoder()

  def encode(string: String, salt: Object): String = {

    val saltedPassword: String = string + "{" + salt.toString + "}"

    val hash = md.digest(saltedPassword.getBytes("UTF-8"))
    val hexString = new StringBuffer()

    for (b <- hash) {
      val hex = Integer.toHexString(0xff & b)
      if (hex.length() == 1) hexString.append('0')
      hexString.append(hex)
    }

    hexString.toString
  }

  def isValid(encoded: String, string: String, salt: Object): Boolean = {
    encode(string, salt).equals(encoded)
  }

}
