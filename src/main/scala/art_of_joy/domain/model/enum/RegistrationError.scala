package art_of_joy.domain.model.`enum`

enum RegistrationError(val message:String):
  case passwordValidationError extends RegistrationError("Пароль должен иметь хотя бы 1 заглавную букву и хотя бы 1 цифру")
  case emailValidationError extends RegistrationError("Проверьте правильность введного email")
  case emailCheckerError extends RegistrationError("Пользователь с такой почтой уже зарегистрирован")
  case phoneCheckerError extends RegistrationError("Пользователь с таким номеров уже зарегистрирован")