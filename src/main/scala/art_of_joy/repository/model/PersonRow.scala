package art_of_joy.repository.model

import art_of_joy.model.`enum`.Role
import art_of_joy.model.person.ClientPerson
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class PersonRow(surname:Option[String] = None,
                     email:Option[String] = None,
                     phone:Option[String] = None,
                     role:Int = Role.user.ordinal,
                     firstname:Option[String] = None,
                     middlename:Option[String] = None,
                     id:Int = -1,
                     password_hash:Option[String] = None,
                     is_confirm_email:Boolean = false,
                     is_confirm_phone:Boolean = false
                    ){
  def toClientPerson = ClientPerson(
    surname, email, phone,role,firstname, middlename,is_confirm_email,is_confirm_phone, password_hash.nonEmpty
  )
}

object PersonRow{
  implicit val decoder: JsonDecoder[PersonRow] = DeriveJsonDecoder.gen[PersonRow]
  implicit val encoder: JsonEncoder[PersonRow] = DeriveJsonEncoder.gen[PersonRow]
}
