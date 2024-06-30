package art_of_joy.model.category

import zio.json.*

case class SubCategory(id:Long, name:String, category_id:Long)
object SubCategory{
  implicit val decoder: JsonDecoder[SubCategory] = DeriveJsonDecoder.gen[SubCategory]
  implicit val encoder: JsonEncoder[SubCategory] = DeriveJsonEncoder.gen[SubCategory]
}