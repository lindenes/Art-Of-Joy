package art_of_joy.model.`enum`

enum AcceptCodeType(val textValue:String):
  case registration extends AcceptCodeType("Регистрация")
  case authorization extends AcceptCodeType("Авторизация")