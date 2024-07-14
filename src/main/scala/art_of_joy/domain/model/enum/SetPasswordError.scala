package art_of_joy.domain.model.`enum`

enum SetPasswordError(val message:String):
  case notEqual extends SetPasswordError("Пароли не совпадают")
  case oldNewEqual extends SetPasswordError("Старый и новый пароли совпадают")
  case passwordValidationError extends SetPasswordError("Пароль должен иметь хотя бы 1 заглавную букву и хотя бы 1 цифру")