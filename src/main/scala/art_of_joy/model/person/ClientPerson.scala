package art_of_joy.model.person

import art_of_joy.model.`enum`.Role
import zio.json._

case class ClientPerson(
                         surname:Option[String] = None,
                         email:Option[String] = None,
                         phone:Option[String] = None,
                         role:Int = Role.user.ordinal,
                         firstname:Option[String] = None,
                         middlename:Option[String] = None,
                         is_confirm_email:Boolean = false,
                         is_confirm_phone:Boolean = false,
                         isPassword:Boolean
                 )

object ClientPerson{
  implicit val decoder: JsonDecoder[ClientPerson] = DeriveJsonDecoder.gen[ClientPerson]
  implicit val encoder: JsonEncoder[ClientPerson] = DeriveJsonEncoder.gen[ClientPerson]
}