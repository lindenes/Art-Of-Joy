package art_of_joy.model.category
import zio.json._
case class CategoryAdd(names:List[String])
object CategoryAdd{
  implicit val decoder: JsonDecoder[CategoryAdd] = DeriveJsonDecoder.gen[CategoryAdd]
  implicit val encoder: JsonEncoder[CategoryAdd] = DeriveJsonEncoder.gen[CategoryAdd]
}