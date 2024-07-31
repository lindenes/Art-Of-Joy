package art_of_joy.application.http

import art_of_joy.application.model.Errors.*
import art_of_joy.application.model.Request.*
import art_of_joy.application.model.Response._
import art_of_joy.repository.service.product.ProductTable
import art_of_joy.timestampSchema
import sttp.model.StatusCode
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*

object ProductRoute {
  
  val baseEndpoint = endpoint
    .errorOut(
      oneOf[ApplicationError](
        oneOfVariant(statusCode(StatusCode.BadGateway).and(jsonBody[HttpDatabaseError])),
        oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[HttpError]))
      )
    )
  
  val exelRoute =
    baseEndpoint.post
      .in("exel")
      .in(jsonBody[ExelBase64])
      .out(jsonBody[List[ExelProduct]])
      .zServerLogic(exel => AppHandler.parseExel(exel))
    
  val productRoute =
    baseEndpoint.post
      .in("product")
      .in(jsonBody[ProductClientFilter])
      .out(jsonBody[List[ProductHttp]])
      .zServerLogic(filter => AppHandler.getProduct(filter))
    
  val routes = ZioHttpInterpreter().toHttp(exelRoute) ++ ZioHttpInterpreter().toHttp(productRoute)
  
  val endPointList = List(exelRoute, productRoute).map(_.endpoint)
}
