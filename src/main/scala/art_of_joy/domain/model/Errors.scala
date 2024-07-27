package art_of_joy.domain.model

object Errors {
  sealed trait DomainError{
    val message:String
    val exception:Throwable
  }
  
  case class ValidationFields(
                               fieldName: String,
                               message: String,
                             )
  
  case class ValidationError(
                            errorList:List[ValidationFields],
                            exception:Throwable,
                            message:String = ""
                            )extends DomainError
  
  case class StorageError(
                           message: String = "Ошибка сессоного хранилища",
                           exception:Throwable
                         )extends DomainError
  case class DataBaseError(
                          message:String = "Ошибка доступа к базе данных",
                          exception: Throwable
                          ) extends DomainError
  case class EmailServiceError(
                              message:String = "Ошибка в сервисе отправки сообщений на почту",
                              exception: Throwable
                              ) extends DomainError
  
  case class NotFoundError(
                            message:String = "Не найден пользователь",
                            exception: Throwable
                          ) extends DomainError
  case class LoadImageError(
                             message:String = "Ошибка загрузки фотографии",
                             exception: Throwable
                           )extends DomainError
}
