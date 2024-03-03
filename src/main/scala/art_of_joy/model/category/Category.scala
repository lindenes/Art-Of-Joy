package art_of_joy.model.category

import art_of_joy.model.category.Category
import zio.json.*

case class Category(id:Int, name:String)

object Category{
  
  implicit val decoder: JsonDecoder[Category] = DeriveJsonDecoder.gen[Category]
  implicit val encoder: JsonEncoder[Category] = DeriveJsonEncoder.gen[Category]
  
}