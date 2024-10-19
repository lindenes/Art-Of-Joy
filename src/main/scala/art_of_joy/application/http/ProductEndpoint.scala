package art_of_joy.application.http

import art_of_joy.application.model.Errors.*
import art_of_joy.application.model.Request.*
import art_of_joy.application.model.Response.*
import art_of_joy.utils.token

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
        HttpCodec.error[HttpExelLoadError](Status.BadRequest),
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
        HttpCodec.error[HttpError](Status.BadRequest),
        HttpCodec.error[HttpAddPhotoError](Status.BadRequest)
      )

  val productPhotoAddEndpoint =
    Endpoint(RoutePattern.POST / "addPhoto")
      .in[ProductPhotoAdd]
      .out[Unit]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest),
        HttpCodec.error[HttpAddPhotoError](Status.BadRequest)
      )

  val getCartEndpoint =
    Endpoint(RoutePattern.GET / "cart")
      .header(token)
      .out[List[CartProductHttp]]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )

  val addCartEndpoint =
    Endpoint(RoutePattern.POST / "cart")
      .header(token)
      .in[AddToCart]
      .out[Long]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )

  val deleteCartEndpoint =
    Endpoint(RoutePattern.DELETE / "cart")
      .header(token)
      .in[DeleteFromCart]
      .out[Long]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )

  val endpointList = List(
    exelEndpoint, productGetEndpoint, productAddEndpoint, productPhotoAddEndpoint, getCartEndpoint,
    addCartEndpoint, deleteCartEndpoint
  )
}
