package art_of_joy.http

import art_of_joy.model.`enum`.{AcceptCodeType, AuthType, RegistrationError}
import art_of_joy.model.http.*
import art_of_joy.model.person.{AcceptCode, AuthPerson, RegPerson}
import art_of_joy.services.SessionStorageLayer.StorageUser
import art_of_joy.services.interfaces.{SessionStorageTrait, UserTrait}
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
          token <- ZIO.fromOption(req.headers.find(_.headerName == "token").map(_.renderedValue)).mapError(err => new Exception("token not found"))
          sessionStorage <- ZIO.service[SessionStorageTrait]
          _ <- sessionStorage.updateTime(token)
          service <- ZIO.service[UserTrait]
          users <- service.getAllPersons(
            req.url.queryParams.get("startRow").getOrElse("0").toInt,
            req.url.queryParams.get("endRow").map(_.toInt)
          )
        }yield Response.json(users.toJson)
        ).catchAll(err => ZIO.from( Response.badRequest(err.getMessage) ))
    },
    Method.PUT / "registration" -> handler {(req:Request) =>
      (
        for{
          service <- ZIO.service[UserTrait]
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
                case None => Response.json(HttpResponse(false, "Авторизируйтесь заново").toJson)
            case Right(errorList) => ZIO.from( Response.json(HttpListStringResponse(false, errorList).toJson) )
        }yield response
      ).catchAll(err => ZIO.from(Response.json(HttpResponse(false, err.getMessage).toJson)))
    },
    Method.POST / "acceptCode" -> handler {(req:Request) =>
      (
        for{
          service <- ZIO.service[SessionStorageTrait]
          token <- ZIO.fromOption(req.headers.find(_.headerName == "token").map(_.renderedValue)).mapError(err => new Exception("token not found"))
          _ <- service.updateTime(token)
          data <- req.body.asString
          body <- ZIO.fromEither(data.fromJson[AcceptCode]).mapError(err => new Exception("Ошибка парсинга " + err))
          storageUser <- service.get(token)
          response <- storageUser match
            case Some(value) =>
              AcceptCodeType.fromOrdinal(body.acceptCodeType) match
                case AcceptCodeType.registration =>
                  for{
                    userService <- ZIO.service[UserTrait]
                    response <- value.acceptCode.map(_ == body.acceptCode) match
                      case Some(equal) =>
                        if (equal)
                          for{
                            userService <- ZIO.service[UserTrait]
                            person <- userService.addPerson(value.person.copy(is_confirm_email = true))
                            _ <- service.updatePerson(token, person)
                            response <- ZIO.from(Response.json(person.toJson))
                          }yield response
                        else
                          ZIO.from(Response.json(HttpResponse(false, "Неверный код подтверждения").toJson))
                      case None => ZIO.from(Response.json(HttpResponse(false, "Нет кода подтверждения в хранилище").toJson))
                  }yield response
                case AcceptCodeType.authorization =>
                  for{
                    userService <- ZIO.service[UserTrait]
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
          service <- ZIO.service[UserTrait]
          response <- AuthType.fromOrdinal(data.authType) match
            case AuthType.passwordAuth =>
              for{
                email <- ZIO.fromOption(data.email).mapError(err => new Exception("Не ввели почту"))
                password <- ZIO.fromOption(data.password).mapError(err => new Exception("Не ввели пароль"))
                _ <- ZIO.when(isValidPassword(password))(ZIO.fail(new Exception(RegistrationError.passwordValidationError.message)))
                _ <- ZIO.when(isValidEmail(email))(ZIO.fail(new Exception(RegistrationError.emailValidationError.message)))
                person <- service.authUser(email,password)
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
            case _ => ZIO.from(Response.text("Ага нет такого входа"))
        } yield response
      ).catchAll(err => ZIO.from( Response.json(HttpResponse(false, err.getMessage).toJson) ))
    }
  ).sandbox.toHttpApp
}
