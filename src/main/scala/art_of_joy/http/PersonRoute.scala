package art_of_joy.http

import art_of_joy.model.http._
import art_of_joy.model.person.{AuthPerson, RegPerson}
import art_of_joy.services.SessionStorageLayer.StorageUser
import art_of_joy.services.interfaces.{SessionStorageTrait, UserTrait}
import zio.ZIO
import zio.http.*
import zio.json.*
import art_of_joy.utils.*

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
          regInfo <- ZIO.fromEither(body.fromJson[RegPerson]).mapError(err => new Exception(err))
          callBack <- regInfo.password match
            case Some(value) => service.passwordRegistration(regInfo.email, value, regInfo.number)
            case None => service.emailRegistration(regInfo.email, regInfo.number)
          sessionID <- ZIO.from(
            UUID.randomUUID.toString
          )
          sessionStorage <- ZIO.service[SessionStorageTrait]
          _ <- callBack match
            case Left(value) => sessionStorage.put(sessionID, StorageUser(value, new Date().getTime ) )
            case Right(value) => ZIO.unit
        }yield callBack match
          case Left(value) => Response.json(value.toJson).addHeaders(
            Headers(
              Header.Custom("Access-Control-Expose-Headers", "token"),
              Header.Custom("token", sessionID)
            )
          )
          case Right(value) => Response.json(HttpListStringResponse(false, value).toJson)
      ).catchAll(err => ZIO.from(Response.error(Status.BadRequest, err.getMessage)))
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
