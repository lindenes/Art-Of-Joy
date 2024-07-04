package art_of_joy.application.http

import art_of_joy.domain.service.category.Category
import art_of_joy.model.category.*
import art_of_joy.model.http.HttpResponse
import art_of_joy.repository.service.brand.BrandTable
import art_of_joy.domain.service.session.SessionStorage
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
        .in(jsonBody[List[CategoryAdd]])
        .out(jsonBody[HttpResponse])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic( (token, categoryAdd) =>
          (
            for {
              _ <- SessionStorage.updateTime(token)
              response <- Category.addCategories(categoryAdd)
            } yield HttpResponse(message = response)
          ).mapError(err => HttpResponse(false, err.getMessage))
        ),
      endpoint.get
        .in("category")
        .out(jsonBody[List[FullCategory]])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic( _ =>
          Category.getFullCategoryList
            .mapError(err => HttpResponse(false, err.getMessage))
        ),
      endpoint.get
        .in("brand")
        .out(jsonBody[List[BrandRow]])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic(_ =>
          BrandTable.getBrand
            .mapError(err => HttpResponse(false, err.getMessage))
        )
    )
  val routes = ZioHttpInterpreter().toHttp(categoryRoute)
  val endPointList = categoryRoute.map(_.endpoint)
}
