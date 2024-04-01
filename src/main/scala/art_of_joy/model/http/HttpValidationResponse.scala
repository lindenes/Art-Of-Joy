package art_of_joy.model.http
import zio.json._
case class HttpValidationResponse(success:Boolean = false, errorList:List[HttpValidationFields])
object HttpValidationResponse{
  implicit val decoder: JsonDecoder[HttpValidationResponse] = DeriveJsonDecoder.gen[HttpValidationResponse]
  implicit val encoder: JsonEncoder[HttpValidationResponse] = DeriveJsonEncoder.gen[HttpValidationResponse]
}