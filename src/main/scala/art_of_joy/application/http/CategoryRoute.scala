package art_of_joy.application.http

import art_of_joy.Env
import art_of_joy.application.model.Errors.*
import art_of_joy.application.model.Request.*
import art_of_joy.application.model.Response._
import zio.*
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import art_of_joy.utils.*
import sttp.model.*

import javax.sql.DataSource

object CategoryRoute {
  val baseEndpoint = endpoint
    .errorOut(
      oneOf[ApplicationError](
        oneOfVariant(statusCode(StatusCode.BadGateway).and(jsonBody[HttpDatabaseError])),
        oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[HttpError]))
      )
    )
  
  val categoryAddEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] =
    baseEndpoint.post
      .in("category")
      .in(token)
      .in(jsonBody[List[CategoryAdd]])
      .out(jsonBody[List[CategoryHttp]])
      .zServerLogic((token, categoryAdd) => Handler.addCategory(token, categoryAdd))
    
  val getCategoryEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] =
    baseEndpoint.get
      .in("category")
      .out(jsonBody[List[CategoryHttp]])
      .zServerLogic(_ => Handler.getCategory)
    
  val getBrandEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] =
    baseEndpoint.get
      .in("brand")
      .out(jsonBody[List[BrandHttp]])
      .zServerLogic(_ => Handler.getBrand)
    
  val endpointList = List(
    categoryAddEndpoint, getCategoryEndpoint, getBrandEndpoint
  )
  val routes = ZioHttpInterpreter().toHttp(endpointList)
  val endPointList = endpointList.map(_.endpoint)
}
