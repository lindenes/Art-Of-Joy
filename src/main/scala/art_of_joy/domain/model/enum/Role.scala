package art_of_joy.domain.model.`enum`

enum Role(val name:String):
  case admin extends Role("Администратор")
  case user extends Role("Пользователь")
