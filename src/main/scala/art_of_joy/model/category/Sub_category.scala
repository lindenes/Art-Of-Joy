package art_of_joy.model.category

import art_of_joy.model.category.Sub_category
import zio.json.*

case class Sub_category(id:Int, name:String, category_id:Int)
object Sub_category{

  implicit val decoder: JsonDecoder[Sub_category] = DeriveJsonDecoder.gen[Sub_category]
  implicit val encoder: JsonEncoder[Sub_category] = DeriveJsonEncoder.gen[Sub_category]

}