package art_of_joy.http

import art_of_joy.model.http.HttpResponse
import art_of_joy.model.person.AuthPerson
import art_of_joy.services.SessionStorageLayer.StorageUser
import art_of_joy.services.interfaces.{SessionStorageTrait, UserTrait}
import zio.ZIO
import zio.http._
import zio.json._

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
              Header.Custom("token", "sessionID")
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
