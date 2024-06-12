package art_of_joy.http

import art_of_joy.model.category._
import art_of_joy.model.http.HttpResponse
import art_of_joy.services.interfaces.{CategoryTrait, SessionStorageTrait}
import zio.*
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import art_of_joy.utils.*
object CategoryRoute {
  val categoryRoute = List(
      endpoint.post
        .in("category")
        .in(token)
        .in(jsonBody[CategoryAdd])
        .out(jsonBody[HttpResponse])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic( (token, categoryAdd) =>
          (
            for {
              sessionStorage <- ZIO.service[SessionStorageTrait]
              _ <- sessionStorage.updateTime(token)
              service <- ZIO.service[CategoryTrait]
              _ <- service.addCategory(categoryAdd.names)
            } yield HttpResponse(message = "Успешно")
          ).mapError(err => HttpResponse(false, err.getMessage))
        ),
      endpoint.post
        .in("subCategory")
        .in(token)
        .in(jsonBody[List[SubCategoryFromClient]])
        .out(jsonBody[HttpResponse])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic( (token, subCategoryList) =>
          (
            for{
              sessionStorage <- ZIO.service[SessionStorageTrait]
              _ <- sessionStorage.updateTime(token)
              service <- ZIO.service[CategoryTrait]
              _ <- service.addSubCategory(subCategoryList)
            }yield HttpResponse(message = "Успешно")
          ).mapError(err => HttpResponse(false, err.getMessage))
        ),
      endpoint.get
        .in("category")
        .out(jsonBody[List[ClientCategory]])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic( _ =>
          (
            for{
              services <- ZIO.service[CategoryTrait]
              category <- services.getFullCategoryList
            }yield category
          ).mapError(err => HttpResponse(false, err.getMessage))
        ),
      endpoint.get
        .in("brand")
        .out(jsonBody[List[Brand]])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic(_ =>
          (
            for{
              services <- ZIO.service[CategoryTrait]
              brand <- services.getBrandList
            }yield brand
          ).mapError(err => HttpResponse(false, err.getMessage))
        )
    )
  val routes = ZioHttpInterpreter().toHttp(categoryRoute)
}
