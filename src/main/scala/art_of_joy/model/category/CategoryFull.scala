package art_of_joy.model.category

import art_of_joy.model.category.{Category, Subcategory}
import art_of_joy.model.category.CategoryFull
import zio.json.*

case class CategoryFull(category:Category, subCategory:List[Option[Subcategory]])
object CategoryFull{

  implicit val decoder: JsonDecoder[CategoryFull] = DeriveJsonDecoder.gen[CategoryFull]
  implicit val encoder: JsonEncoder[CategoryFull] = DeriveJsonEncoder.gen[CategoryFull]

}