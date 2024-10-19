package art_of_joy.application.model

import zio.json.*
import zio.schema.{Schema, DeriveSchema}
object Errors {
  
  abstract class ApplicationError{
    val message:String
  }
  
  case class HttpValidationFields(
                                   fieldName: String, 
                                   message: String
                                 ) extends ApplicationError
  object HttpValidationFields {
    implicit val schema: Schema[HttpValidationFields] = DeriveSchema.gen
  }
  
  case class HttpValidationError(errorList:List[HttpValidationFields], message: String = "Валидационная ошибка") extends ApplicationError
  object HttpValidationError {
    implicit val schema: Schema[HttpValidationError] = DeriveSchema.gen
  }
  
  case class HttpDatabaseError(
                              message:String = "Внутренняя ошибка сервера",
                              applicationMessage: String
                              ) extends ApplicationError

  object HttpDatabaseError {
    implicit val schema: Schema[HttpDatabaseError] = DeriveSchema.gen
  }
  
  case class HttpError(
                      message:String = "Ошибка запроса",
                      applicationMessage:String
                      ) extends ApplicationError

  object HttpError {
    implicit val schema: Schema[HttpError] = DeriveSchema.gen
  }

  case class HttpNotFoundUser(
                        message: String = "Не найден пользователь в базе данных",
                        applicationMessage: String
                      ) extends ApplicationError
  object HttpNotFoundUser {
    implicit val schema: Schema[HttpNotFoundUser] = DeriveSchema.gen
  }

  case class HttpExelLoadError(
                               message: String = "Ошибка загрузки данных exel",
                               applicationMessage: String
                             ) extends ApplicationError

  object HttpExelLoadError {
    implicit val schema: Schema[HttpExelLoadError] = DeriveSchema.gen
  }
  
  case class HttpAddPhotoError(
                            message: String = "Фото не добавлено, неизвестная ошибка",
                            applicationMessage: String
                          ) extends ApplicationError

  object HttpAddPhotoError {
    implicit val schema: Schema[HttpAddPhotoError] = DeriveSchema.gen
  }
  
}
