package art_of_joy.application.http

import art_of_joy.application.model.Errors._
import art_of_joy.application.model.ProductClientFilter
import art_of_joy.domain.model.ExelProduct
import art_of_joy.domain.service.exel.Exel
import art_of_joy.model.product.ExelBase64
import art_of_joy.repository.model.ProductRow
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
      .zServerLogic(exel => Handler.parseExel(exel))
    
  val productRoute =
    baseEndpoint.post
      .in("product")
      .in(jsonBody[ProductClientFilter])
      .out(jsonBody[List[ProductRow]])
      .zServerLogic(filter => Handler.getProduct(filter))
    
  val routes = ZioHttpInterpreter().toHttp(exelRoute) ++ ZioHttpInterpreter().toHttp(productRoute)
  
  val endPointList = List(exelRoute, productRoute).map(_.endpoint)
}
