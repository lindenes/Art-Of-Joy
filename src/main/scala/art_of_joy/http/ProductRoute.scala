package art_of_joy.http

import art_of_joy.services.interfaces.ExelTrait
import zio.ZIO
import zio.http.*
import zio.json.*
object ProductRoute {
  def getRoutes = Routes(
    Method.POST / "exel" -> handler {(req:Request) =>
      (
        for{
          data <- req.body.asMultipartForm.map(_.map)
          exel <- ZIO.fromOption(data.get("exelData")).mapError(err => new Exception("Нет данных exel в поле запроса exelData"))
          arrayByte <- exel.asChunk.map(_.toArray)
          service <- ZIO.service[ExelTrait]
          exelProduct <- service.getProductFromExel(arrayByte)
        }yield Response.json(exelProduct.toJson)
      ).catchAll(err => ZIO.from(Response.text(err.getMessage)))
    }
  ).sandbox.toHttpApp
}
