package lemyr.http

import lemyr.services.interfaces.*
import zio.*
import zio.http.*
import zio.json.*
import zio.http.Header.{AccessControlAllowMethods, AccessControlAllowOrigin, Origin}
import zio.http.Middleware.{CorsConfig, cors}
import io.getquill.*
import lemyr.model.category.{Category, SubCategoryFromClient}
import lemyr.model.http.HttpResponse
import lemyr.model.person.AuthPerson
import lemyr.services.SessionStorageLayer.StorageUser
import zio.json.ast.{Json, JsonCursor}

import java.util.{Date, UUID}
object Route {

  val config: CorsConfig =
    CorsConfig(
      allowedOrigin = {
        case origin@Origin.Value(_, host, _) => Some(AccessControlAllowOrigin.Specific(origin))
        case origin@_ => Some(AccessControlAllowOrigin.Specific(origin))

      },
      allowedMethods = AccessControlAllowMethods(Method.PUT, Method.POST, Method.GET, Method.DELETE),
    )
  def getRoutes = Routes(
    Method.PUT / "category" -> handler{ (req:Request) =>
      (
        for{
          token <- ZIO.fromOption(req.headers.find(_.headerName == "token").map(_.renderedValue)).mapError(err => new Exception("token not found"))
          sessionStorage <- ZIO.service[SessionStorageTrait]
          _ <- sessionStorage.updateTime(token)
          data <- req.body.asString
          body <- ZIO.from(data.fromJson[List[Json]]).mapError(err => new Exception(err))
          names <- ZIO.from( body.map(field => {
            field.get(JsonCursor.field("name")) match
              case Right(value) => value.asString
              case Left(value) => None
          }).collect{case Some(value) => value}
          )
          service <- ZIO.service[CategoryTrait]
          _ <- service.addCategory(names)
        }yield Response.json(HttpResponse(message = "Успешно").toJson)
      ).catchAll(err => ZIO.from(Response.error(Status.BadRequest, err.getMessage)))
    },
    Method.PUT / "subCategory" -> handler{ (req:Request) =>
      (
        for{
          token <- ZIO.fromOption(req.headers.find(_.headerName == "token").map(_.renderedValue)).mapError(err => new Exception("token not found"))
          sessionStorage <- ZIO.service[SessionStorageTrait]
          _ <- sessionStorage.updateTime(token)
          service <- ZIO.service[CategoryTrait]
          data <- req.body.asString
          subCategoryList <- ZIO.fromEither(data.fromJson[List[SubCategoryFromClient]]).mapError(err => new Exception(err))
          _ <- service.addSubCategory(subCategoryList)
        }yield Response.json(HttpResponse(message = "Успешно").toJson)
      ).catchAll(err => ZIO.from(Response.error(Status.BadRequest, err.getMessage)))
    },
    Method.GET / "category" -> Handler.fromZIO(
      for{
        services <- ZIO.service[CategoryTrait]
        category <- services.getFullCategoryList
      }yield Response.json(category.toJson)
    ),
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
  ).sandbox.toHttpApp @@ cors(config)
}
