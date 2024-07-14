package art_of_joy.domain.model.`enum`

enum AuthType(val textValue:String):
  case passwordAuth extends AuthType("Вход по паролю")
  case emailAuth extends AuthType("Вход по почте")
  case phoneAuth extends AuthType("Вход по номеру телефона")
  case tokenAuth extends AuthType("Авторизация по токену")