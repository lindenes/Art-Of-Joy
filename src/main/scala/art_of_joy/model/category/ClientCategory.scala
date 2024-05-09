package art_of_joy.model.category
import zio.json._

case class ClientCategory(id:Int, name:String, subcategory: List[Subcategory])
object ClientCategory{
  implicit val decoder: JsonDecoder[ClientCategory] = DeriveJsonDecoder.gen[ClientCategory]
  implicit val encoder: JsonEncoder[ClientCategory] = DeriveJsonEncoder.gen[ClientCategory]
}