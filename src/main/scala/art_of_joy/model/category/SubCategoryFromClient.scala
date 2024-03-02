package lemyr.model.category

import zio.json._

case class SubCategoryFromClient(name:String, categoryID:Int)
object SubCategoryFromClient{
  implicit val decoder: JsonDecoder[SubCategoryFromClient] = DeriveJsonDecoder.gen[SubCategoryFromClient]
  implicit val encoder: JsonEncoder[SubCategoryFromClient] = DeriveJsonEncoder.gen[SubCategoryFromClient]
}