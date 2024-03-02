package lemyr.model.category

import lemyr.model.category.{Category, Sub_category}
import lemyr.model.category.CategoryFull
import zio.json.*

case class CategoryFull(category:Category, subCategory:List[Option[Sub_category]])
object CategoryFull{

  implicit val decoder: JsonDecoder[CategoryFull] = DeriveJsonDecoder.gen[CategoryFull]
  implicit val encoder: JsonEncoder[CategoryFull] = DeriveJsonEncoder.gen[CategoryFull]

}