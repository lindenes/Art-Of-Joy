package art_of_joy.model.http
import zio.json._
case class HttpValidationFields(fieldName:String, message:String) extends Exception(message)
object HttpValidationFields{
  implicit val decoder: JsonDecoder[HttpValidationFields] = DeriveJsonDecoder.gen[HttpValidationFields]
  implicit val encoder: JsonEncoder[HttpValidationFields] = DeriveJsonEncoder.gen[HttpValidationFields]
}