package art_of_joy.model.category

import zio.json._

case class FullCategory(id:Long, name:String, subcategory: List[SubCategory])
object FullCategory{
  implicit val decoder: JsonDecoder[FullCategory] = DeriveJsonDecoder.gen[FullCategory]
  implicit val encoder: JsonEncoder[FullCategory] = DeriveJsonEncoder.gen[FullCategory]
}