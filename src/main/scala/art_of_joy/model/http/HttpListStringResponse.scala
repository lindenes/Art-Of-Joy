package art_of_joy.model.http

import zio.json._

case class HttpListStringResponse(success:Boolean = true, data:List[String])
object HttpListStringResponse{
  
  implicit def decoder: JsonDecoder[HttpListStringResponse] = DeriveJsonDecoder.gen[HttpListStringResponse]
  implicit def encoder: JsonEncoder[HttpListStringResponse] = DeriveJsonEncoder.gen[HttpListStringResponse]
  
}
