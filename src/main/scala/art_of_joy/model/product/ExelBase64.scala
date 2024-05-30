package art_of_joy.model.product
import zio.json._
case class ExelBase64(exelData:String)
object ExelBase64{
  implicit val decoder: JsonDecoder[ExelBase64] = DeriveJsonDecoder.gen[ExelBase64]
  implicit val encoder: JsonEncoder[ExelBase64] = DeriveJsonEncoder.gen[ExelBase64]
}