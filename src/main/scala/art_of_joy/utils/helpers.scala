package lemyr.utils

import java.security.MessageDigest

def passToHash(password: String): String = {
  val md = MessageDigest.getInstance("SHA-256")
  val bytes = md.digest(password.getBytes("UTF-8"))
  bytes.map("%02x".format(_)).mkString.toUpperCase
}
