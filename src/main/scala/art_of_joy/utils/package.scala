package art_of_joy

import java.security.MessageDigest
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
  
}
