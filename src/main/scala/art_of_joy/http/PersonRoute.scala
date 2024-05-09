package art_of_joy.http

import art_of_joy.model.`enum`._
import art_of_joy.model.http.*
import art_of_joy.model.person._
import art_of_joy.services.SessionStorageLayer.StoragePerson
import art_of_joy.services.interfaces.{PersonTrait, SessionStorageTrait}
import zio.ZIO
import zio.http.*
import zio.json.*
import art_of_joy.utils.*
import zio.json.ast.{Json, JsonCursor}

import java.util.{Date, UUID}

object PersonRoute {
  def getRoutes = Routes(
    Method.GET / "person" -> handler{ (req:Request) =>
      (
        for{
          token <- getToken(req)
          sessionStorage <- ZIO.service[SessionStorageTrait]
          _ <- sessionStorage.updateTime(token)
          service <- ZIO.service[PersonTrait]
          users <- service.getAllPersons(
            req.url.queryParams.get("startRow").getOrElse("0").toInt,
            req.url.queryParams.get("endRow").map(_.toInt)
          )
        }yield Response.json(users.toJson)
        ).catchAll(err => ZIO.from( Response.json(HttpResponse(false, err.getMessage).toJson) ))
    },
    Method.POST / "registration" -> handler {(req:Request) =>
      (
        for{
          service <- ZIO.service[PersonTrait]
          body <- req.body.asString
          regInfo <- ZIO.fromEither(body.fromJson[RegPerson]).mapError(err => new Exception("Ошибка парсинга " + err))
          _ <- ZIO.when(regInfo.email.isEmpty && regInfo.phone.isEmpty)(ZIO.fail(new Exception("Не передана почта или номер телефона для регистрации")))
          callBack <- regInfo.email match
            case Some(value) => service.emailRegistration(value)
            case None => service.phoneRegistration(regInfo.phone.get)
          response <- callBack match
            case Left(sessionID) =>
              for{
                storage <- ZIO.service[SessionStorageTrait]
                result <- storage.get(sessionID)
              }yield result match
                case Some(storagePerson) => Response.json(
                  s"""{"token":"$sessionID"}""")
                case None => Response.json(HttpResponse(false, "Авторизируйтесь заново").toJson)
            case Right(errorList) => ZIO.from( Response.json(HttpValidationResponse(false, errorList).toJson) )
        }yield response
      ).catchAll(err => ZIO.from(Response.json(HttpResponse(false, err.getMessage).toJson)))
    },
    Method.POST / "acceptCode" -> handler {(req:Request) =>
      (
        for{
          service <- ZIO.service[SessionStorageTrait]
          token <- getToken(req)
          _ <- service.updateTime(token)
          data <- req.body.asString
          body <- ZIO.fromEither(data.fromJson[AcceptCode]).mapError(err => new Exception("Ошибка парсинга " + err))
          storagePerson <- service.get(token)
          response <- storagePerson match
            case Some(value) =>
              AcceptCodeType.fromOrdinal(body.acceptCodeType) match
                case AcceptCodeType.registration =>
                  for{
                    userService <- ZIO.service[PersonTrait]
                    response <-
                        if (value.acceptCode == body.acceptCode)
                          for{
                            userService <- ZIO.service[PersonTrait]
                            person <- userService.addPerson(value.person.copy(is_confirm_email = true))
                            _ <- service.updatePerson(token, person)
                            response <- ZIO.from(Response.json(person.toJson))
                          }yield response
                        else
                          ZIO.from(Response.json(
                            HttpValidationResponse(false, List(
                              HttpValidationFields("acceptCode", "Неверный код подтверждения")
                            )).toJson
                          ))
                  }yield response
                case AcceptCodeType.authorization =>
                  for{
                    userService <- ZIO.service[PersonTrait]
                    email <- ZIO.fromOption(value.person.email).mapError(err => new Exception("В хранилище не найден email"))
                    person <- userService.getPersonByEmail(email)
                    _ <- ZIO.when(person.length > 1)(ZIO.fail(new Exception("С такой почтой несколько пользователей")))
                    response <- ZIO.from(Response.json(person.head.toJson))
                  }yield response
            case None => ZIO.from(Response.json(HttpResponse(false, "Авторизируйтесь заново").toJson))
        }yield response
      ).catchAll(err => ZIO.from(Response.json(HttpResponse(false, err.getMessage).toJson)))
    },
    Method.POST / "authorization" -> handler { (req: Request) =>
      (
        for {
          body <- req.body.asString
          data <- ZIO.fromEither(body.fromJson[AuthPerson]).mapError(err => new Exception("Ошибка парсинга " +err))
          sessionStorage <- ZIO.service[SessionStorageTrait]
          service <- ZIO.service[PersonTrait]
          response <- AuthType.fromOrdinal(data.authType) match
            case AuthType.passwordAuth =>
              for{
                email <- ZIO.fromOption(data.email).mapError(err => new Exception("Не ввели почту"))
                password <- ZIO.fromOption(data.password).mapError(err => new Exception("Не ввели пароль"))
                _ <- ZIO.when(!isValidEmail(email))(ZIO.fail(HttpValidationFields("email_authFormTI", RegistrationError.emailValidationError.message)))
                personByEmail <- service.getPersonByEmail(email)
                _ <- ZIO.when(personByEmail.isEmpty)(ZIO.fail(HttpValidationFields("email_authFormTI", "Пользователь с такой почтой не зарегистрирован")))
                passwordByEmail <- service.getPersonByEmail(email).map(_.head.password_hash)
                _ <- ZIO.when(passwordByEmail.isEmpty)(ZIO.fail(HttpValidationFields("password_authFormTI","У пользователя не установлен пароль")))
                _ <- ZIO.when(!isValidPassword(password))(ZIO.fail(HttpValidationFields("password_authFormTI",RegistrationError.passwordValidationError.message)))
                person <- service.authPerson(email,password)
                result <- if person.isEmpty
                  then ZIO.from(Response.json(
                    HttpValidationResponse(false, List(HttpValidationFields("email_authFormTI", "Пользователь с такой почтой не зарегистрирован"))).toJson
                  ))
                else 
                  ZIO.from(Response.json(
                    person.map( p =>
                      ClientPerson(p.surname, p.email,p.phone, p.role, p.firstname, p.middlename, p.is_confirm_email, p.is_confirm_phone, p.password_hash.nonEmpty)
                    ).head.toJson
                  ))
              }yield result
            case AuthType.emailAuth =>
              for{
                email <- ZIO.fromOption(data.email).mapError(err => new Exception("Не ввели почту"))
                token <- service.authPersonOnEmail(email)
                result <- ZIO.from(
                  Response.json(
                    s"""{"token":"$token"}""")
                )
              }yield result
            case AuthType.phoneAuth => ZIO.from(Response.text("ага щас нет такого входа еще"))
        } yield response
      ).catchAll {
        case value:HttpValidationFields =>
          ZIO.from(Response.json(HttpValidationResponse(false, List(value)).toJson))
        case otherError =>
          ZIO.from(Response.json(HttpResponse(false, otherError.getMessage).toJson))
      }
    },
    Method.POST / "password" -> handler { (req:Request) =>
      (
        for{
          storage <- ZIO.service[SessionStorageTrait]
          token <- getToken(req)
          _ <- storage.updateTime(token)
          body <- req.body.asString
          setPassword <- ZIO.from(body.fromJson[SetPassword]).mapError(err => new Exception("Ошибка парсинга " + err))
          currentPerson <- storage.get(token).flatMap(ZIO.fromOption(_)).mapError(ex => new Exception("Авторизируйтесь заново"))
          personService <- ZIO.service[PersonTrait]
          updatePass <- setPassword.oldPassword match
            case Some(password) => personService.updatePassword(
              currentPerson.person.id,
              setPassword.password,
              setPassword.repeatPassword,
              password
            )
            case None => personService.setPassword(
              currentPerson.person.id,
              setPassword.password,
              setPassword.repeatPassword
            )
          response <- updatePass match
            case Left(value) =>
              if value >= 1 then ZIO.from( Response.json(HttpResponse(true, "Данные обновлены").toJson) )
              else ZIO.from( Response.json(HttpResponse(false, "Не получилось найти нужного пользователя").toJson) )
            case Right(value) =>
              ZIO.from(Response.json(HttpValidationResponse(false, value).toJson))
        }yield response
      ).catchAll(ex =>  ZIO.from( Response.json(HttpResponse(false, ex.getMessage).toJson)))
    },
    Method.POST / "personInfo" -> handler { (req:Request) =>
      (
        for{
          storage <- ZIO.service[SessionStorageTrait]
          token <- getToken(req)
          _ <- storage.updateTime(token)
          body <- req.body.asString
          updatePersonInfo <- ZIO.from(body.fromJson[UpdatePersonInfo]).mapError(err => new Exception("Ошибка парсинга " + err))
          currentPerson <- storage.get(token).flatMap(ZIO.fromOption(_)).mapError(err => new Exception("Авторизируйтесь заново"))
          personStorage <- ZIO.service[PersonTrait]
          updatedRows <- personStorage.setPersonInfo(
            currentPerson.person.id,
            updatePersonInfo.surname,
            updatePersonInfo.firstname,
            updatePersonInfo.middlename
          )
          response <- if updatedRows >= 1
            then ZIO.from(
              Response.json(HttpResponse(true, "Данные обновлены").toJson)
            )
          else ZIO.from(
            Response.json(HttpResponse(false, "Не получилось найти нужного пользователя").toJson)
          )
        }yield response
      ).catchAll(ex =>  ZIO.from( Response.json(HttpResponse(false, ex.getMessage).toJson)))
    }
  ).sandbox.toHttpApp
}
