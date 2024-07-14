package art_of_joy.application.model
import zio.json._
object Http {
  case class HttpResponse(success: Boolean = true, message: String)

  object HttpResponse {
    implicit val decoder: JsonDecoder[HttpResponse] = DeriveJsonDecoder.gen[HttpResponse]
    implicit val encoder: JsonEncoder[HttpResponse] = DeriveJsonEncoder.gen[HttpResponse]
  }

  case class HttpValidationFields(fieldName: String, message: String) extends Exception(message)

  object HttpValidationFields {
    implicit val decoder: JsonDecoder[HttpValidationFields] = DeriveJsonDecoder.gen[HttpValidationFields]
    implicit val encoder: JsonEncoder[HttpValidationFields] = DeriveJsonEncoder.gen[HttpValidationFields]
  }

  case class HttpValidationResponse(success: Boolean = false, errorList: List[HttpValidationFields])

  object HttpValidationResponse {
    implicit val decoder: JsonDecoder[HttpValidationResponse] = DeriveJsonDecoder.gen[HttpValidationResponse]
    implicit val encoder: JsonEncoder[HttpValidationResponse] = DeriveJsonEncoder.gen[HttpValidationResponse]
  }
}
