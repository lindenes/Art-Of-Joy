package art_of_joy.application.http


import art_of_joy.application.model.Errors.*
import art_of_joy.application.model.Request.*
import art_of_joy.application.model.Response.*
import art_of_joy.utils.*

import zio._
import zio.http._
import zio.http.codec.PathCodec._
import zio.http.codec._
import zio.http.endpoint._

object CategoryEndpoint {

  val categoryAddEndpoint =
    Endpoint(RoutePattern.POST / "category" ?? Doc.p("Route for querying books"))
      .header(token)
      .in[List[CategoryAdd]]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )
      .out[List[CategoryHttp]]
    
  val getCategoryEndpoint =
    Endpoint(RoutePattern.GET / "category" ?? Doc.p("Route for querying books"))
      .out[List[CategoryHttp]]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )
    
  val getBrandEndpoint =
    Endpoint(RoutePattern.GET / "brand" ?? Doc.p("Route for querying books"))
      .out[List[BrandHttp]]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )

  val brandAddEndpoint =
    Endpoint(RoutePattern.POST / "brand" ?? Doc.p("Route for querying books"))
      .in[BrandAdd]
      .out[BrandHttp]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )
    
  val endpointList = List(
    categoryAddEndpoint, getCategoryEndpoint, getBrandEndpoint,brandAddEndpoint
  )

}
