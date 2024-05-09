package art_of_joy.model.person
import zio.json._
case class UpdatePersonInfo(surname:String,
                            firstname:String, 
                            middlename:Option[String] = None)
object UpdatePersonInfo{
  implicit val decoder: JsonDecoder[UpdatePersonInfo] = DeriveJsonDecoder.gen[UpdatePersonInfo]
  implicit val encoder: JsonEncoder[UpdatePersonInfo] = DeriveJsonEncoder.gen[UpdatePersonInfo]
}