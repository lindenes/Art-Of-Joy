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
  
  val exelEndpoint =
    baseEndpoint.post
      .in("exel")
      .in(jsonBody[ExelBase64])
      .out(jsonBody[List[ExelProduct]])
      .zServerLogic(exel => AppHandler.parseExel(exel))
    
  val productGetEndpoint =
    baseEndpoint.post
      .in("getProduct")
      .in(jsonBody[ProductClientFilter])
      .out(jsonBody[List[ProductHttp]])
      .zServerLogic(filter => AppHandler.getProduct(filter))

  val productAddEndpoint =
    baseEndpoint.post
      .in("addProduct")
      .in(jsonBody[ProductAdd])
      .out(statusCode(StatusCode.Ok))
      .zServerLogic(p => AppHandler.addProduct(p))

  val productPhotoAddEndpoint =
    baseEndpoint.post
      .in("addPhoto")
      .in(jsonBody[ProductPhotoAdd])
      .out(statusCode(StatusCode.Ok))
      .zServerLogic(data => AppHandler.addProductPhoto(data.id, data.binaryData))

  val routes = ZioHttpInterpreter().toHttp(exelEndpoint) ++ ZioHttpInterpreter().toHttp(
    List(
      productGetEndpoint, productPhotoAddEndpoint
    )
  )

  val endPointList = List(exelEndpoint, productGetEndpoint).map(_.endpoint)
}
