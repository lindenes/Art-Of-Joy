package art_of_joy.application.http


import art_of_joy.application.model.CategoryApplication.*
import art_of_joy.application.model.Http.*
import art_of_joy.domain.service.CategoryService
import art_of_joy.domain.service.session.SessionStorage
import art_of_joy.repository.service.category.CategoryTable
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
              response <- 
                CategoryService.addCategories(categoryAdd)
                  .map(addedData =>
                    s"Добавлено ${addedData.length} категорий и ${addedData.flatMap(_._2).length} подкатегории"
                  )
            } yield HttpResponse(message = response)
          ).mapError(err => HttpResponse(false, err.getMessage))
        ),
      endpoint.get
        .in("category")
        .out(jsonBody[List[CategoryHttp]])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic( _ =>
          CategoryService.getCategories
            .map(categoryList => 
              categoryList.map(category => 
                CategoryHttp(
                  category.id, category.name,
                  category.subCategories.map(sub => SubCategoryHttp(sub.id, sub.name, sub.categoryId))
                )
              )
            )
            .mapError(err => HttpResponse(false, err.getMessage))
        ),
      endpoint.get
        .in("brand")
        .out(jsonBody[List[BrandHttp]])
        .errorOut(jsonBody[HttpResponse])
        .zServerLogic(_ =>
          CategoryTable.getBrands
            .map(_.map(b => BrandHttp(b.id, b.name)))
            .mapError(err => HttpResponse(false, err.getMessage))
        )
    )
  val routes = ZioHttpInterpreter().toHttp(categoryRoute)
  val endPointList = categoryRoute.map(_.endpoint)
}
