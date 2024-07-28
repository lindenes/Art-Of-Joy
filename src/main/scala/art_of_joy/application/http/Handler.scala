package art_of_joy.application.http

import art_of_joy.Env
import art_of_joy.application.model.CategoryApplication.{BrandHttp, CategoryAdd, CategoryHttp, SubCategoryHttp}
import art_of_joy.application.model.Errors.*
import art_of_joy.application.model.PersonApplication.*
import art_of_joy.application.model.ProductClientFilter
import art_of_joy.domain.model.Errors.*
import art_of_joy.domain.model.StoragePerson
import art_of_joy._
import art_of_joy.domain.service.exel.Exel
import art_of_joy.domain.service.{CategoryService, PersonService}
import art_of_joy.domain.service.session.SessionStorage
import art_of_joy.model.product.ExelBase64
import art_of_joy.repository.service.category.CategoryTable
import art_of_joy.repository.service.person.PersonTable
import art_of_joy.repository.service.product.ProductTable
import art_of_joy.utils.*
import zio.*

import java.util.{Base64, Date, UUID}
import javax.sql.DataSource

object Handler {

  def checkForTokenAuth(token:Option[String], authType:Int) =
    AuthType.fromOrdinal(authType) match
      case AuthType.tokenAuth if token.nonEmpty =>
        for{
          user <- SessionStorage.get(token.get)
          result <- user match
            case None => ZIO.fail(HttpError("Не найден пользователь", "user not found in session storage"))
            case Some(value) => ZIO.succeed(token.get -> authType)
        }yield result
      case AuthType.tokenAuth if token.isEmpty => ZIO.fail(HttpError("Не найден токен", "token not found"))
      case _ => ZIO.succeed("" -> authType)


  def getUserList(token:String, startRow:Int, endRow:Option[Int]): ZIO[Env & Scope, ApplicationError, List[PersonHttp]] =
    (
      for {
        _ <- SessionStorage.updateTime(token)
        users <-
          PersonService.getAllPersons(startRow, endRow)
            .map(_.map(person =>
              PersonHttp(
                person.surname, person.email, person.phone, person.role.ordinal, person.firstname,
                person.middleName, person.id, person.passwordHash.nonEmpty,
                person.isConfirmEmail, person.isConfirmPhone
              )
            ))
      } yield users
    ).mapError{
      case error:DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
      case _ => HttpError(applicationMessage = "unknown error")
    }

  def registration(regInfo:RegPerson): ZIO[Env, ApplicationError, (String, String)] =
    (
      for {
        _ <- ZIO.when(regInfo.email.isEmpty && regInfo.phone.isEmpty)(ZIO.fail(HttpError("Нужно указать номер телефона или почту","not found phone or email ")))
        sessionID <- regInfo.email match
          case Some(value) => PersonService.emailRegistration(value.toLowerCase)
          case None => PersonService.phoneRegistration(regInfo.phone.get)
        response <-
          SessionStorage.get(sessionID).map {
            case Some(storagePerson) => (sessionID, "token")
            case None => ("", "")
          }
      } yield response
    )
      .mapError{
        case error:ValidationError => HttpValidationError(
          error.errorList.map(i => HttpValidationFields(i.fieldName, i.message))
        )
        case error:StorageError => HttpError(error.message, error.exception.getMessage)
        case error:DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
        case er:HttpError => er
        case _ => HttpError(applicationMessage = "unknown error")
      }

  def authorization(clientPerson:AuthPerson,token:String, authType:Int): ZIO[Env & Scope & DataSource, ApplicationError,  (Option[PersonHttp], String, String)] =
    (
      AuthType.fromOrdinal(authType) match
        case AuthType.passwordAuth =>
          for {
            email <- ZIO.fromOption(clientPerson.email).mapError(_ => HttpError("Нужно указать почту", "not found phone or email"))
            password <- ZIO.fromOption(clientPerson.password).mapError(_ => HttpError("Нужно указать пароль", "not found password"))
            _ <- ZIO.when(!isValidEmail(email))(ZIO.fail(HttpValidationError(List(HttpValidationFields("email_authFormTI", RegistrationError.emailValidationError.message)))))
            personByEmail <- PersonService.getPersonByEmail(email)
            _ <- ZIO.when(personByEmail.isEmpty)(ZIO.fail(HttpValidationError(List(HttpValidationFields("email_authFormTI", "Пользователь с такой почтой не зарегистрирован")))))
            passwordByEmail <- PersonService.getPersonByEmail(email).map(_.head.passwordHash)
            _ <- ZIO.when(passwordByEmail.isEmpty)(ZIO.fail(HttpValidationError(List(HttpValidationFields("password_authFormTI", "У пользователя не установлен пароль")))))
            _ <- ZIO.when(!isValidPassword(password))(ZIO.fail(HttpValidationError(List(HttpValidationFields("password_authFormTI", RegistrationError.passwordValidationError.message)))))
            person <- PersonService.authPerson(email, password)
            token <- ZIO.succeed(UUID.randomUUID.toString)
            _ <- SessionStorage.put(token, StoragePerson(person.head, new Date().getTime))
            result <- if person.isEmpty
            then ZIO.fail(
                HttpValidationError(
                  List(HttpValidationFields("email_authFormTI", "Пользователь с такой почтой не зарегистрирован"))
                )
              )
            else
              ZIO.succeed(
                (person.map(person =>
                  PersonHttp(
                    person.surname, person.email, person.phone, person.role.ordinal,
                    person.firstname, person.middleName, person.id, person.passwordHash.nonEmpty,
                    person.isConfirmEmail, person.isConfirmPhone
                  )
                ).headOption, token, "Token")
              )
          } yield result
        case AuthType.emailAuth =>
          for {
            email <- ZIO.fromOption(clientPerson.email).mapError(_ => HttpError("Нужно указать почту", "not found phone or email "))
            token <- PersonService.authPersonOnEmail(email)
            result <- ZIO.succeed(Option.empty[PersonHttp], "", "")
          } yield result
        case AuthType.phoneAuth => ???
        case AuthType.tokenAuth =>
          for {
            storageUser <- SessionStorage.get(token)
            _ <- SessionStorage.updateTime(token)
          } yield (storageUser.map(user =>
              PersonHttp(
                user.person.surname, user.person.email, user.person.phone, user.person.role.ordinal,
                user.person.firstname, user.person.middleName, user.person.id, user.person.passwordHash.nonEmpty,
                user.person.isConfirmEmail, user.person.isConfirmPhone
            )), token, "Token")
    ).mapError{
      case error:DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
      case er:HttpValidationError => er
      case er: HttpError => er
      case _ => HttpError(applicationMessage = "unknown error")
    }

  def checkAcceptCode(acceptCode:AcceptCode, token:String): ZIO[Env & Scope, ApplicationError, PersonHttp] =
    (
      for {
        _ <- SessionStorage.updateTime(token)
        storagePerson <- SessionStorage.get(token)
        response <- storagePerson match
          case Some(value) =>
            AcceptCodeType.fromOrdinal(acceptCode.acceptCodeType) match
              case AcceptCodeType.registration =>
                if (value.acceptCode == acceptCode.acceptCode)
                  for {
                    person <- PersonService.addPerson(value.person.copy(isConfirmEmail = true))
                    _ <- SessionStorage.updatePerson(token, person)
                    response <- ZIO.succeed(
                      PersonHttp(
                        person.surname, person.email, person.phone, person.role.ordinal,
                        person.firstname, person.middleName, person.id, person.passwordHash.nonEmpty,
                        person.isConfirmEmail, person.isConfirmPhone
                      )
                    )
                  } yield response
                else
                  ZIO.fail(HttpValidationError(List(
                    HttpValidationFields("acceptCode", "Неверный код подтверждения")
                  )))
              case AcceptCodeType.authorization =>
                for {
                  email <- ZIO.succeed(value.person.email)
                  person <-
                    PersonService.getPersonByEmail(email)
                      .map(_.map(person =>
                        PersonHttp(
                          person.surname, person.email, person.phone, person.role.ordinal,
                          person.firstname, person.middleName, person.id, person.passwordHash.nonEmpty,
                          person.isConfirmEmail, person.isConfirmPhone
                        )
                      ))
                  _ <- ZIO.when(person.length > 1)(ZIO.fail(HttpError("Найдено несколько пользователей с такой почтой", "Found several users with this email in database")))
                  response <- ZIO.from(person.head)
                } yield response
          case None => ZIO.fail(HttpError("авторизируйтесь заново", "not found user in storage"))
      } yield response
    )
      .mapError{
      case error:StorageError => HttpError(error.message, error.exception.getMessage)
      case error:DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
      case er:HttpError => er
      case er:Throwable => HttpError(applicationMessage = er.getMessage)
      case _ => HttpError(applicationMessage = "unknown error")
    }

  def setPassword(setPassword: SetPassword, token:String): ZIO[Env & DataSource & Scope, ApplicationError, Unit] =
    (
      for {
        _ <- SessionStorage.updateTime(token)
        currentPerson <- SessionStorage.get(token).flatMap(ZIO.fromOption(_)).mapError(_ => HttpError("авторизируйтесь заново", "not found user in storage"))
        updatePass <- setPassword.oldPassword match
          case Some(password) => PersonService.updatePassword(
            currentPerson.person.id,
            setPassword.password,
            setPassword.repeatPassword,
            password
          )
          case None => PersonService.setPassword(
            currentPerson.person.id,
            setPassword.password,
            setPassword.repeatPassword
          )
      } yield ()
    ).mapError{
      case error: StorageError => HttpError(error.message, error.exception.getMessage)
      case error: DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
      case error: NotFoundError => HttpNotFoundUser(applicationMessage = error.exception.getMessage)
      case er: HttpError => er
      case _ => HttpError(applicationMessage = "unknown error")
    }

  def setPersonInfo(updatePersonInfo: UpdatePersonInfo, token:String): ZIO[Env & DataSource & Scope, ApplicationError, Unit] =
    (
      for {
        _ <- SessionStorage.updateTime(token)
        currentPerson <- SessionStorage.get(token).flatMap(ZIO.fromOption(_)).mapError(_ => HttpError("авторизируйтесь заново", "not found user in storage"))
        updatedRows <- PersonService.setPersonInfo(
          currentPerson.person.id,
          updatePersonInfo.surname,
          updatePersonInfo.firstname,
          updatePersonInfo.middleName
        )
      } yield ()
    ).mapError{
      case error: StorageError => HttpError(error.message, error.exception.getMessage)
      case error: DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
      case error: NotFoundError => HttpNotFoundUser(applicationMessage = error.exception.getMessage)
      case er: HttpError => er
      case _ => HttpError(applicationMessage = "unknown error")
    }

  def addCategory(token:String, categoryAdd:List[CategoryAdd]) =
    (
      for {
        _ <- SessionStorage.updateTime(token)
        response <-
          CategoryService.addCategories(categoryAdd)
            .map(_.map{case (category, subList) =>
                CategoryHttp(
                  category.id, category.name,
                  subList.map(sub =>
                    SubCategoryHttp(sub.id, sub.name, sub.categoryId)
                  )
                )
              }
            )
      } yield response
    ).mapError{
      case error: StorageError => HttpError(error.message, error.exception.getMessage)
      case error: DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
      case _ => HttpError(applicationMessage = "unknown error")
    }

  def getCategory =
    CategoryService.getCategories
      .map(categoryList =>
        categoryList.map(category =>
          CategoryHttp(
            category.id, category.name,
            category.subCategories.map(sub => SubCategoryHttp(sub.id, sub.name, sub.categoryId))
          )
        )
      )
      .mapError{
        case error: StorageError => HttpError(error.message, error.exception.getMessage)
        case error: DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
        case _ => HttpError(applicationMessage = "unknown error")
      }

  def getBrand =
    CategoryTable.getBrands
      .map(_.map(b => BrandHttp(b.id, b.name)))
      .mapError{
        case error: StorageError => HttpError(error.message, error.exception.getMessage)
        case error: DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
        case _ => HttpError(applicationMessage = "unknown error")
      }

  def parseExel(exel:ExelBase64) =
    Exel.getProductFromExel(Base64.getDecoder.decode(exel.exelData))
      .mapError{
        case er:LoadImageError => HttpExelLoadError(applicationMessage = er.exception.getMessage)
        case _ => HttpError(applicationMessage = "unknown error")
      }

  def getProduct(filter: ProductClientFilter) =
    ProductTable.getProductList(filter)
      .mapError{
        case error: DataBaseError => HttpDatabaseError(applicationMessage = error.exception.getMessage)
        case _ => HttpError(applicationMessage = "unknown error")
      }
}
