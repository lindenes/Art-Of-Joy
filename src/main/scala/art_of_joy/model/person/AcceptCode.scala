package art_of_joy.model.person

import zio.json._

case class AcceptCode(acceptCode:String, acceptCodeType:Int)
object AcceptCode{
  implicit val decoder: JsonDecoder[AcceptCode] = DeriveJsonDecoder.gen[AcceptCode]
  implicit val encoder: JsonEncoder[AcceptCode] = DeriveJsonEncoder.gen[AcceptCode]
}