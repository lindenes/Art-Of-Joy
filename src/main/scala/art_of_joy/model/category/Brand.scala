package art_of_joy.model.category

import zio.json._
case class Brand(id:Int, name:String)
object Brand{
  implicit val decoder: JsonDecoder[Brand] = DeriveJsonDecoder.gen[Brand]
  implicit val encoder: JsonEncoder[Brand] = DeriveJsonEncoder.gen[Brand]
}