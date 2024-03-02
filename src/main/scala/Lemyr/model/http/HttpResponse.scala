package lemyr.model.http

import zio.json._

case class HttpResponse(success:Boolean = true, message:String)
object HttpResponse{
  implicit val decoder: JsonDecoder[HttpResponse] = DeriveJsonDecoder.gen[HttpResponse]
  implicit val encoder: JsonEncoder[HttpResponse] = DeriveJsonEncoder.gen[HttpResponse]
}