package art_of_joy.model.person

import art_of_joy.model.person.Person
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Person(surname:Option[String] = None,
                  email:Option[String] = None,
                  phone:Option[String] = None,
                  role:Int = 2,
                  firstname:Option[String] = None,
                  middlename:Option[String] = None,
                  id:Int = -1,
                  password_hash:Option[String] = None,
                  is_confirm_email:Boolean = false,
                  is_confirm_phone:Boolean = false
                 )

object Person{
  implicit val decoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]
  implicit val encoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
}