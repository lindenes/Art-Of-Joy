package art_of_joy.model.category

import art_of_joy.model.category.Subcategory
import zio.json.*

case class Subcategory(id:Int, name:String, category_id:Int)
object Subcategory{

  implicit val decoder: JsonDecoder[Subcategory] = DeriveJsonDecoder.gen[Subcategory]
  implicit val encoder: JsonEncoder[Subcategory] = DeriveJsonEncoder.gen[Subcategory]

}