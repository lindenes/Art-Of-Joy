package art_of_joy.application.http

import art_of_joy.application.model.ProductClientFilter
import art_of_joy.domain.model.ExelProduct
import art_of_joy.domain.service.exel.Exel
import art_of_joy.model.http.HttpResponse
import art_of_joy.model.product.{ExelBase64, Product}
import art_of_joy.repository.model.ProductRow
import art_of_joy.repository.service.product.ProductTable
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
        Exel.getProductFromExel(Base64.getDecoder.decode(exel.exelData))
          .mapError(err => HttpResponse(false, err.getMessage))
      )
  val productRoute =
    endpoint.post
      .in("product")
      .in(jsonBody[ProductClientFilter])
      .out(jsonBody[List[ProductRow]])
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic(filter =>
        ProductTable.getProductList(filter)
          .mapError(err => HttpResponse(false, err.getMessage))
      )
  val routes = ZioHttpInterpreter().toHttp(exelRoute) ++ ZioHttpInterpreter().toHttp(productRoute)
  val endPointList = List(exelRoute, productRoute).map(_.endpoint)
}
