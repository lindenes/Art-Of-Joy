package art_of_joy.http

import art_of_joy.model.http.HttpResponse
import art_of_joy.model.product.{ExelBase64, ExelProduct, Product, ProductClientFilter}
import art_of_joy.services.interfaces.{ExelService, ProductService}
import art_of_joy.timestampSchema
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import zio.http.Method

import java.util.Base64
object ProductRoute {
  val exelRoute =
    endpoint.post
      .in("exel")
      .in(jsonBody[ExelBase64])
      .out(jsonBody[List[ExelProduct]])
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic(exel =>
        ExelService.getProductFromExel(Base64.getDecoder.decode(exel.exelData))
          .mapError(err => HttpResponse(false, err.getMessage))
      )
  val productRoute =
    endpoint.post
      .in("product")
      .in(jsonBody[ProductClientFilter])
      .out(jsonBody[List[Product]])
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic(filter =>
        ProductService.getProductList(filter)
          .mapError(err => HttpResponse(false, err.getMessage))
      )
  val routes = ZioHttpInterpreter().toHttp(exelRoute) ++ ZioHttpInterpreter().toHttp(productRoute)
  val endPointList = List(exelRoute, productRoute).map(_.endpoint)
}
