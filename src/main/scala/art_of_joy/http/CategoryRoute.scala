package art_of_joy.http

import art_of_joy.model.category.SubCategoryFromClient
import art_of_joy.model.http.HttpResponse
import art_of_joy.services.interfaces.{CategoryTrait, SessionStorageTrait}
import zio.http.*
import zio.*
import zio.json.ast.{Json, JsonCursor}
import zio.json._
object CategoryRoute {
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
        }yield Response.json(HttpResponse(message = List("Успешно")).toJson)
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
        }yield Response.json(HttpResponse(message = List("Успешно")).toJson)
        ).catchAll(err => ZIO.from(Response.error(Status.BadRequest, err.getMessage)))
    },
    Method.GET / "category" -> Handler.fromZIO(
      for{
        services <- ZIO.service[CategoryTrait]
        category <- services.getFullCategoryList
      }yield Response.json(category.toJson)
    )
  ).sandbox.toHttpApp
}
