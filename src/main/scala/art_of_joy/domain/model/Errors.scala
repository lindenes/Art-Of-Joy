package art_of_joy.domain.model

object Errors {
  sealed trait DomainError{
    val message:String
  }
  
  case class ValidationFields(
                               fieldName: String,
                               message: String,
                             ) extends DomainError
  
  case class ValidationError(
                            errorList:List[ValidationFields],
                            message:String = ""
                            )extends DomainError
  
  case class StorageError(
                           message: String = "Ошибка сессоного хранилища",
                         )extends DomainError
  case class DataBaseError(
                          message:String = "Ошибка доступа к базе данных",
                          ) extends DomainError
  case class EmailServiceError(
                              message:String = "Ошибка в сервисе отправки сообщений на почту",
                              ) extends DomainError
  
  case class NotFoundError(
                            message:String = "Не найден пользователь",
                          ) extends DomainError
  case class LoadImageError(
                             message:String = "Ошибка загрузки фотографии",
                           )extends DomainError

  case class LoadExelDataError(
                                message: String = "Ошибка обработки exel",
                              ) extends DomainError
}
