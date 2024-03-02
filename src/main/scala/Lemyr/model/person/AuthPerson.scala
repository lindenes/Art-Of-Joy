package lemyr.model.person

import lemyr.model.person.AuthPerson
import zio.json.*

case class AuthPerson(email:String, password:String)
object AuthPerson{
  implicit val decoder: JsonDecoder[AuthPerson] = DeriveJsonDecoder.gen[AuthPerson]
  implicit val encoder: JsonEncoder[AuthPerson] = DeriveJsonEncoder.gen[AuthPerson]
}