package art_of_joy.model.person

import art_of_joy.model.person.Person
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Person(surname:Option[String],
                  email:String,
                  phone:String,
                  role:Int,
                  firstname:Option[String],
                  middlename:Option[String],
                  id:Int,
                  password:Option[String],
                  is_confirm_email:Boolean,
                  is_confirm_phone:Boolean
                 )

object Person{
  implicit val decoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]
  implicit val encoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
}