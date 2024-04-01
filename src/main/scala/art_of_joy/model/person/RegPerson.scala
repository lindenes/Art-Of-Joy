package art_of_joy.model.person

import zio.json._
case class RegPerson(email:Option[String], phone:Option[String])
object RegPerson{
  implicit val decoder: JsonDecoder[RegPerson] = DeriveJsonDecoder.gen[RegPerson]
  implicit val encoder: JsonEncoder[RegPerson] = DeriveJsonEncoder.gen[RegPerson]
}
