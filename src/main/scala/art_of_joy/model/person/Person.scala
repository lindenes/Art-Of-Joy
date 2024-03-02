package lemyr.model.person

import lemyr.model.person.Person
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Person(surname:String,email:String, number:String, role:Int, firstname:String, middlename:String,  id:Int, password:String)

object Person{
  implicit val decoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]
  implicit val encoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
}