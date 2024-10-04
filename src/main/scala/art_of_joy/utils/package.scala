package art_of_joy

import zio.ZIO
import zio.http.Request
import zio.http.codec.HeaderCodec

import java.security.MessageDigest
import java.util.Random
import java.util.regex.Pattern
package object utils {

  def passToHash(password: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(password.getBytes("UTF-8"))
    bytes.map("%02x".format(_)).mkString.toUpperCase
  }

  def isValidEmail(email: String): Boolean = 
    Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$")
      .matcher(email)
      .matches()
  
  def isValidPassword(password: String): Boolean = 
    Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$")
      .matcher(password)
      .matches()
  def generateCode =
    val chars = "0123456789"
    val random = new Random()
    (
      for (_ <- 1 to 6)
        yield chars(random.nextInt(chars.length))
    ).mkString
    
  val token = HeaderCodec.name[String]("token")
}
