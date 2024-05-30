package art_of_joy.http

import art_of_joy.model.product.{ExelBase64, ProductClientFilter}
import art_of_joy.services.interfaces.{ExelTrait, ProductTrait}
import zio.ZIO
import zio.http.*
import zio.json.*

import java.util.Base64
object ProductRoute {
  def getRoutes = Routes(
    Method.POST / "exel" -> handler {(req:Request) =>
      (
        for{
          body <- req.body.asString
          exel <- ZIO.fromEither(body.fromJson[ExelBase64]).mapError(err => new Exception("Ошибка парсинга" + err))
          service <- ZIO.service[ExelTrait]
          exelProduct <- service.getProductFromExel(Base64.getDecoder.decode(exel.exelData))
        }yield Response.json(exelProduct.toJson)
      ).catchAll(err => ZIO.from(Response.text(err.getMessage)))
    },
    Method.POST / "product" -> handler {(req:Request) =>
      (
        for{
          body <- req.body.asString
          filter <- ZIO.fromEither(body.fromJson[ProductClientFilter]).mapError(err => new Exception("Ошибка парсинга" + err))
          service <- ZIO.service[ProductTrait]
          product <- service.getProductList(filter)
        }yield Response.json(product.toJson)
      ).catchAll(err => ZIO.from(Response.text(err.getMessage)))
    }
  ).sandbox.toHttpApp
}
