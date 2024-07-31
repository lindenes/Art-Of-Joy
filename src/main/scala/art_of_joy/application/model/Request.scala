package art_of_joy.application.model

import zio.json._

object Request {
  
  case class CategoryAdd(name: String, subNames: List[String])
  object CategoryAdd {
    implicit val decoder: JsonDecoder[CategoryAdd] = DeriveJsonDecoder.gen[CategoryAdd]
    implicit val encoder: JsonEncoder[CategoryAdd] = DeriveJsonEncoder.gen[CategoryAdd]
  }

  case class ProductClientFilter(
                                  subCategoryID: Option[Int],
                                  brandID: Option[Int],
                                  maxPrice: Option[Double],
                                  minPrice: Option[Double],
                                  name: Option[String]
                                )
  object ProductClientFilter {
    implicit val decoder: JsonDecoder[ProductClientFilter] = DeriveJsonDecoder.gen[ProductClientFilter]
    implicit val encoder: JsonEncoder[ProductClientFilter] = DeriveJsonEncoder.gen[ProductClientFilter]
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
  
  case class AcceptCode(acceptCode: String, acceptCodeType: Int)
  object AcceptCode {
    implicit val decoder: JsonDecoder[AcceptCode] = DeriveJsonDecoder.gen[AcceptCode]
    implicit val encoder: JsonEncoder[AcceptCode] = DeriveJsonEncoder.gen[AcceptCode]
  }

  case class UpdatePersonInfo(surname: String,
                              firstname: String,
                              middleName: Option[String] = None)
  object UpdatePersonInfo {
    implicit val decoder: JsonDecoder[UpdatePersonInfo] = DeriveJsonDecoder.gen[UpdatePersonInfo]
    implicit val encoder: JsonEncoder[UpdatePersonInfo] = DeriveJsonEncoder.gen[UpdatePersonInfo]
  }

  case class SetPassword(password: String, repeatPassword: String, oldPassword: Option[String])
  object SetPassword {
    implicit val decoder: JsonDecoder[SetPassword] = DeriveJsonDecoder.gen[SetPassword]
    implicit val encoder: JsonEncoder[SetPassword] = DeriveJsonEncoder.gen[SetPassword]
  }

  case class ExelBase64(exelData: String)
  object ExelBase64 {
    implicit val decoder: JsonDecoder[ExelBase64] = DeriveJsonDecoder.gen[ExelBase64]
    implicit val encoder: JsonEncoder[ExelBase64] = DeriveJsonEncoder.gen[ExelBase64]
  }
}
