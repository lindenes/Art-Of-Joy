import art_of_joy.ApplicationConfig.SmtpConfig
import art_of_joy.domain.service.session.SessionStorage
import art_of_joy.repository.service.category.CategoryTable
import art_of_joy.repository.service.person.PersonTable
import io.getquill.*

import javax.sql.DataSource

package object art_of_joy {
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  
  type Env = DataSource & PersonTable & SessionStorage & CategoryTable & SmtpConfig

  enum AcceptCodeType(val textValue: String):
    case registration extends AcceptCodeType("Регистрация")
    case authorization extends AcceptCodeType("Авторизация")

  enum AuthType(val textValue: String):
    case passwordAuth extends AuthType("Вход по паролю")
    case emailAuth extends AuthType("Вход по почте")
    case phoneAuth extends AuthType("Вход по номеру телефона")
    case tokenAuth extends AuthType("Авторизация по токену")

  enum ExelField(val fieldName: String):
    case article extends ExelField("Артикул продавца")
    case articleWB extends ExelField("Артикул WB")
    case subcategory extends ExelField("Предмет")
    case category extends ExelField("Категория")
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

  enum RegistrationError(val message: String):
    case passwordValidationError extends RegistrationError("Пароль должен иметь хотя бы 1 заглавную букву и хотя бы 1 цифру")
    case emailValidationError extends RegistrationError("Проверьте правильность введного email")
    case emailCheckerError extends RegistrationError("Пользователь с такой почтой уже зарегистрирован")
    case phoneCheckerError extends RegistrationError("Пользователь с таким номеров уже зарегистрирован")

  enum Role(val name: String):
    case admin extends Role("Администратор")
    case user extends Role("Пользователь")

  enum SetPasswordError(val message: String):
    case notEqual extends SetPasswordError("Пароли не совпадают")
    case oldNewEqual extends SetPasswordError("Старый и новый пароли совпадают")
    case passwordValidationError extends SetPasswordError("Пароль должен иметь хотя бы 1 заглавную букву и хотя бы 1 цифру")
}
