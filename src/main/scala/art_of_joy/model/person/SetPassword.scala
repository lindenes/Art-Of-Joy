package art_of_joy.model.person
import zio.json._
case class SetPassword(password:String, repeatPassword:String, oldPassword:Option[String])
object SetPassword{
  implicit val decoder: JsonDecoder[SetPassword] = DeriveJsonDecoder.gen[SetPassword]
  implicit val encoder: JsonEncoder[SetPassword] = DeriveJsonEncoder.gen[SetPassword]
}