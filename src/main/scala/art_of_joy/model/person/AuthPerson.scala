package art_of_joy.model.person

import art_of_joy.model.person.AuthPerson
import zio.json.*

case class AuthPerson(email:String, password:String)
object AuthPerson{
  implicit val decoder: JsonDecoder[AuthPerson] = DeriveJsonDecoder.gen[AuthPerson]
  implicit val encoder: JsonEncoder[AuthPerson] = DeriveJsonEncoder.gen[AuthPerson]
}