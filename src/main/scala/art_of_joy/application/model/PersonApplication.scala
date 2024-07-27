package art_of_joy.application.model
import zio.json.*
object PersonApplication {
  
  case class PersonHttp(
                         surname: Option[String],
                         email: String,
                         phone: Option[String],
                         role: Int,
                         firstname: Option[String],
                         middleName: Option[String],
                         id: Long,
                         havePassword:Boolean,
                         isConfirmEmail: Boolean,
                         isConfirmPhone: Boolean
                       )
  object PersonHttp {
    implicit val decoder: JsonDecoder[PersonHttp] = DeriveJsonDecoder.gen[PersonHttp]
    implicit val encoder: JsonEncoder[PersonHttp] = DeriveJsonEncoder.gen[PersonHttp]
  }

  case class AuthPerson(email: Option[String], password: Option[String], phone: Option[String])

  object AuthPerson {
    implicit val decoder: JsonDecoder[AuthPerson] = DeriveJsonDecoder.gen[AuthPerson]
    implicit val encoder: JsonEncoder[AuthPerson] = DeriveJsonEncoder.gen[AuthPerson]
  }

  case class RegPerson(email: Option[String], phone: Option[String])

  object RegPerson {
    implicit val decoder: JsonDecoder[RegPerson] = DeriveJsonDecoder.gen[RegPerson]
    implicit val encoder: JsonEncoder[RegPerson] = DeriveJsonEncoder.gen[RegPerson]
  }

  case class SetPassword(password: String, repeatPassword: String, oldPassword: Option[String])

  object SetPassword {
    implicit val decoder: JsonDecoder[SetPassword] = DeriveJsonDecoder.gen[SetPassword]
    implicit val encoder: JsonEncoder[SetPassword] = DeriveJsonEncoder.gen[SetPassword]
  }

  case class UpdatePersonInfo(surname: String,
                              firstname: String,
                              middleName: Option[String] = None)

  object UpdatePersonInfo {
    implicit val decoder: JsonDecoder[UpdatePersonInfo] = DeriveJsonDecoder.gen[UpdatePersonInfo]
    implicit val encoder: JsonEncoder[UpdatePersonInfo] = DeriveJsonEncoder.gen[UpdatePersonInfo]
  }
  
  case class AcceptCode(acceptCode: String, acceptCodeType: Int)

  object AcceptCode {
    implicit val decoder: JsonDecoder[AcceptCode] = DeriveJsonDecoder.gen[AcceptCode]
    implicit val encoder: JsonEncoder[AcceptCode] = DeriveJsonEncoder.gen[AcceptCode]
  }
}
