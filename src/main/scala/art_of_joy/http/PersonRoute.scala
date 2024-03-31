package art_of_joy.http

import art_of_joy.model.http.*
import art_of_joy.model.person.{AuthPerson, RegPerson}
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
    Method.PUT / "person" -> handler {(req:Request) =>
      (
        for{
          service <- ZIO.service[UserTrait]
          body <- req.body.asString
          regInfo <- ZIO.fromEither(body.fromJson[RegPerson]).mapError(err => new Exception("Ошибка парсинга " + err))
          _ <- ZIO.when(regInfo.email.isEmpty && regInfo.number.isEmpty)(ZIO.fail(new Exception("Не передана почта или номер телефона для регистрации")))
          callBack <- regInfo.email match
            case Some(value) => service.emailRegistration(value)
            case None => service.numberRegistration(regInfo.number.get)
          response <- callBack match
            case Left(sessionID) =>
              for{
                storage <- ZIO.service[SessionStorageTrait]
                result <- storage.get(sessionID)
              }yield result match
                case Some(storagePerson) => Response.json(
                  s""" "sessionID":"$sessionID" """)
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
          body <- ZIO.from(data.fromJson[Json]).mapError(err => new Exception("Ошибка парсинга "+err))
          acceptCode <- ZIO.from(
            body.get(JsonCursor.field("acceptCode")) match
              case Right(value) => value.asString
              case Left(value) => None
          ).mapError(err => new Exception("Не найдено поле acceptCode"))
          storageUser <- service.get(token)
          response <- storageUser match
            case Some(value) =>
              for{
                userService <- ZIO.service[UserTrait]
                response <- value.acceptCode.map(_ == acceptCode) match
                  case Some(equal) => 
                    if (equal)
                      for{
                        userService <- ZIO.service[UserTrait]
                        person <- userService.addPerson(value.person.copy(is_confirm_email = true))
                        _ <- service.updatePerson(token, person)
                        response <- ZIO.from(Response.json(person.toJson))
                      }yield response
                    else
                      ZIO.from(Response.json(HttpResponse(false, "Кеверный код подтверждения").toJson))
                  case None => ZIO.from(Response.json(HttpResponse(false, "Нет кода подтверждения в хранилище").toJson))
              }yield response
            case None => ZIO.from(Response.json(HttpResponse(false, "Авторизируйтесь заново").toJson))
        }yield response
      ).catchAll(err => ZIO.from(Response.json(HttpResponse(false, err.getMessage).toJson)))
    },
    Method.POST / "person" -> handler { (req: Request) =>
      (
        for {
          body <- req.body.asString
          data <- ZIO.fromEither(body.fromJson[AuthPerson]).mapError(err => new Exception(err))
          sessionStorage <- ZIO.service[SessionStorageTrait]
          service <- ZIO.service[UserTrait]
          user <- service.authUser(data.email, data.password)
          sessionID <- ZIO.from(
            UUID.randomUUID.toString
          )
          _ <- if user.length == 1
          then sessionStorage.put(sessionID, StorageUser(user.head, new Date().getTime ) )
          else ZIO.unit
        } yield {
          if user.length == 1
          then Response.json(user.head.toJson).addHeaders(
            Headers(
              Header.Custom("Access-Control-Expose-Headers", "token"),
              Header.Custom("token", sessionID)
            )
          )
          else if user.length > 1
          then Response.json(HttpResponse(false, "Зарегистрировано несколько пользователей").toJson)
          else
            Response.json(HttpResponse(false, "Вы не зарегестрированы").toJson)
        }
        ).catchAll(err => ZIO.from( Response.error(Status.BadRequest, err.getMessage) ))

    }
  ).sandbox.toHttpApp
}
