package art_of_joy.http

import art_of_joy.model.http.HttpResponse
import art_of_joy.model.product.{ExelBase64, ExelProduct, Product, ProductClientFilter}
import art_of_joy.services.interfaces.{ExelTrait, ProductTrait}
import zio.ZIO
import art_of_joy.timestampSchema
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*

import java.util.Base64
object ProductRoute {
  val exelRoute =
    endpoint.post
      .in("exel")
      .in(jsonBody[ExelBase64])
      .out(jsonBody[List[ExelProduct]])
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic(exel =>
        (
          for{
            service <- ZIO.service[ExelTrait]
            exelProduct <- service.getProductFromExel(Base64.getDecoder.decode(exel.exelData))
          }yield exelProduct
        ).mapError(err => HttpResponse(false, err.getMessage))
      )
  val productRoute =
    endpoint.post
      .in("product")
      .in(jsonBody[ProductClientFilter])
      .out(jsonBody[List[Product]])
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic(filter =>
        (
          for{
            service <- ZIO.service[ProductTrait]
            product <- service.getProductList(filter)
          }yield product
        ).mapError(err => HttpResponse(false, err.getMessage))
      )
  val routes = ZioHttpInterpreter().toHttp(exelRoute) ++ ZioHttpInterpreter().toHttp(productRoute)
}
