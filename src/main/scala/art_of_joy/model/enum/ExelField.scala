package art_of_joy.model.`enum`

enum ExelField(val fieldName:String):
  case article extends ExelField("Артикул продавца")
  case articleWB extends ExelField("Артикул WB")
  case subcategory extends ExelField("Предмет")
  case brand extends ExelField("Бренд")
  case name extends ExelField("Наименование")
  case description extends ExelField("Описание")
  case size extends ExelField("Размер")
  case ruSize extends ExelField("Рос. размер")
  case barcode extends ExelField("Баркод товара")
  case material extends ExelField("Материал изделия")
  case fragility extends ExelField("Хрупкость")
  case mediaFile extends ExelField("Медиафайлы")
  case width extends ExelField("Ширина предмета, см")
  case height extends ExelField("Высота предмета, см")
  case color extends ExelField("Цвет")
  case productCountry extends ExelField("Страна производства")