package art_of_joy.application.model

import zio.json._
object Errors {
  
  sealed trait ApplicationError{
    val message:String
  }
  
  case class HttpValidationFields(fieldName: String, message: String) extends ApplicationError
  object HttpValidationFields {
    implicit val decoder: JsonDecoder[HttpValidationFields] = DeriveJsonDecoder.gen[HttpValidationFields]
    implicit val encoder: JsonEncoder[HttpValidationFields] = DeriveJsonEncoder.gen[HttpValidationFields]
  }
  
  case class HttpValidationError(errorList:List[HttpValidationFields], message: String = "Валидационная ошибка") extends ApplicationError
  object HttpValidationError {
    implicit val decoder: JsonDecoder[HttpValidationError] = DeriveJsonDecoder.gen[HttpValidationError]
    implicit val encoder: JsonEncoder[HttpValidationError] = DeriveJsonEncoder.gen[HttpValidationError]
  }
  
  case class HttpDatabaseError(
                              message:String = "Внутренняя ошибка сервера",
                              applicationMessage: String
                              ) extends ApplicationError

  object HttpDatabaseError {
    implicit val decoder: JsonDecoder[HttpDatabaseError] = DeriveJsonDecoder.gen[HttpDatabaseError]
    implicit val encoder: JsonEncoder[HttpDatabaseError] = DeriveJsonEncoder.gen[HttpDatabaseError]
  }
  
  case class HttpError(
                      message:String = "Ошибка запроса",
                      applicationMessage:String
                      ) extends ApplicationError

  object HttpError {
    implicit val decoder: JsonDecoder[HttpError] = DeriveJsonDecoder.gen[HttpError]
    implicit val encoder: JsonEncoder[HttpError] = DeriveJsonEncoder.gen[HttpError]
  }

  case class HttpNotFoundUser(
                        message: String = "Не найден пользователь в базе данных",
                        applicationMessage: String
                      ) extends ApplicationError
  object HttpNotFoundUser {
    implicit val decoder: JsonDecoder[HttpNotFoundUser] = DeriveJsonDecoder.gen[HttpNotFoundUser]
    implicit val encoder: JsonEncoder[HttpNotFoundUser] = DeriveJsonEncoder.gen[HttpNotFoundUser]
  }

  case class HttpExelLoadError(
                               message: String = "Ошибка загрузки данных exel",
                               applicationMessage: String
                             ) extends ApplicationError

  object HttpExelLoadError {
    implicit val decoder: JsonDecoder[HttpExelLoadError] = DeriveJsonDecoder.gen[HttpExelLoadError]
    implicit val encoder: JsonEncoder[HttpExelLoadError] = DeriveJsonEncoder.gen[HttpExelLoadError]
  }
  
  case class HttpAddPhotoError(
                            message: String = "Фото не добавлено, неизвестная ошибка",
                            applicationMessage: String
                          ) extends ApplicationError

  object AddPhotoError {
    implicit val decoder: JsonDecoder[HttpAddPhotoError] = DeriveJsonDecoder.gen[HttpAddPhotoError]
    implicit val encoder: JsonEncoder[HttpAddPhotoError] = DeriveJsonEncoder.gen[HttpAddPhotoError]
  }
  
}
