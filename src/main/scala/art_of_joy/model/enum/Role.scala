package art_of_joy.model.`enum`

enum Role(val name:String):
  case admin extends Role("Администратор")
  case user extends Role("Пользователь")
