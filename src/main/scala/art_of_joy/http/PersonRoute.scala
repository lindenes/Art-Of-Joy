package art_of_joy.http

import art_of_joy.model.`enum`.{AcceptCodeType, AuthType, RegistrationError}
import art_of_joy.model.http.*
import art_of_joy.model.person.{AcceptCode, AuthPerson, SetPassword, RegPerson}
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
        ).catchAll(err => ZIO.from( Response.badRequest(err.getMessage) ))
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
                  s""" "token":"$sessionID" """)
                case None => Response.json(HttpResponse(false, List("Авторизируйтесь заново")).toJson)
            case Right(errorList) => ZIO.from( Response.json(HttpValidationResponse(false, errorList).toJson) )
        }yield response
      ).catchAll(err => ZIO.from(Response.json(HttpResponse(false, List(err.getMessage)).toJson)))
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
                    response <- value.acceptCode.map(_ == body.acceptCode) match
                      case Some(equal) =>
                        if (equal)
                          for{
                            userService <- ZIO.service[PersonTrait]
                            person <- userService.addPerson(value.person.copy(is_confirm_email = true))
                            _ <- service.updatePerson(token, person)
                            response <- ZIO.from(Response.json(person.toJson))
                          }yield response
                        else
                          ZIO.from(Response.json(HttpResponse(false, List("Неверный код подтверждения")).toJson))
                      case None => ZIO.from(Response.json(HttpResponse(false, List("Нет кода подтверждения в хранилище")).toJson))
                  }yield response
                case AcceptCodeType.authorization =>
                  for{
                    userService <- ZIO.service[PersonTrait]
                    email <- ZIO.fromOption(value.person.email).mapError(err => new Exception("В хранилище не найден email"))
                    person <- userService.getPersonByEmail(email)
                    _ <- ZIO.when(person.length > 1)(ZIO.fail(new Exception("С такой почтой несколько пользователей")))
                    response <- ZIO.from(Response.json(person.head.toJson))
                  }yield response
            case None => ZIO.from(Response.json(HttpResponse(false, List("Авторизируйтесь заново")).toJson))
        }yield response
      ).catchAll(err => ZIO.from(Response.json(HttpResponse(false, List(err.getMessage)).toJson)))
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
                _ <- ZIO.when(isValidPassword(password))(ZIO.fail(new Exception(RegistrationError.passwordValidationError.message)))
                _ <- ZIO.when(isValidEmail(email))(ZIO.fail(new Exception(RegistrationError.emailValidationError.message)))
                person <- service.authPerson(email,password)
                result <- ZIO.from(Response.json(person.toJson))
              }yield result
            case AuthType.emailAuth =>
              for{
                email <- ZIO.fromOption(data.email).mapError(err => new Exception("Не ввели почту"))
                token <- service.authUserOnEmail(email)
                result <- ZIO.from(
                  Response.json(
                    s""" "token":"$token" """)
                )
              }yield result
            case AuthType.phoneAuth => ZIO.from(Response.text("ага щас нет такого входа еще"))
        } yield response
      ).catchAll(err => ZIO.from( Response.json(HttpResponse(false, List(err.getMessage)).toJson) ))
    }
  ).sandbox.toHttpApp
  Method.POST / "password" -> handler { (req:Request) =>
    (
      for{
        storage <- ZIO.service[SessionStorageTrait]
        token <- getToken(req)
        _ <- storage.updateTime(token)
        body <- req.body.asString
        password <- ZIO.from(body.fromJson[SetPassword]).mapError(err => new Exception("Ошибка парсинга " + err))
        userService <- ZIO.service[PersonTrait]
        response <- ZIO.from(Response.text(""))
      }yield response
    ).catchAll(ex =>  ZIO.from( Response.json(HttpResponse(false, List(ex.getMessage)).toJson)))
  }
}
