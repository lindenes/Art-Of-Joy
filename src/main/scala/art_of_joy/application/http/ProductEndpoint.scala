package art_of_joy.application.http

import art_of_joy.application.model.Errors.*
import art_of_joy.application.model.Request.*
import art_of_joy.application.model.Response.*
import zio.http.{RoutePattern, Status}
import zio.http.codec.HttpCodec
import zio.http.endpoint.Endpoint

object ProductEndpoint {
  
  val exelEndpoint =
    Endpoint(RoutePattern.POST / "exel")
      .in[ExelBase64]
      .out[List[ExelProduct]]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )
    
  val productGetEndpoint =
    Endpoint(RoutePattern.POST / "getProduct")
      .in[ProductClientFilter]
      .out[List[ProductHttp]]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )

  val productAddEndpoint =
    Endpoint(RoutePattern.POST / "addProduct")
      .in[ProductAdd]
      .out[Unit]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )

  val productPhotoAddEndpoint =
    Endpoint(RoutePattern.POST / "addPhoto")
      .in[ProductPhotoAdd]
      .out[Unit]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )
  
  val endpointList = List(
    exelEndpoint, productGetEndpoint, productAddEndpoint, productPhotoAddEndpoint
  )
}
