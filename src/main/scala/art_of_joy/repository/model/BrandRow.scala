package art_of_joy.model.category

import zio.json._
case class BrandRow(id:Int, name:String)
object BrandRow{
  implicit val decoder: JsonDecoder[BrandRow] = DeriveJsonDecoder.gen[BrandRow]
  implicit val encoder: JsonEncoder[BrandRow] = DeriveJsonEncoder.gen[BrandRow]
}