package art_of_joy.application.http

import art_of_joy.Env
import art_of_joy.application.model.Errors.*
import art_of_joy.*
import zio.{Scope, ZIO}
import art_of_joy.utils.*
import zio.json.ast.{Json, JsonCursor}
import art_of_joy.application.model.Request.*
import art_of_joy.application.model.Response.*
import art_of_joy.application.service.AppHandler
import zio.http.{RoutePattern, Status}
import zio.http.codec.{HeaderCodec, HttpCodec}
import zio.http.endpoint.Endpoint

import java.util.{Date, UUID}
import javax.sql.DataSource

object PersonEndpoint {

  val personEndpoint =
    Endpoint(RoutePattern.GET / "person")
    .header(token)
    .query(HttpCodec.query[Int]("startRow"))
    .query(HttpCodec.query[Option[Int]]("endRow"))
    .out[List[PersonHttp]]
    .outErrors[ApplicationError](
      HttpCodec.error[HttpDatabaseError](Status.BadGateway),
      HttpCodec.error[HttpError](Status.BadRequest)
    )

  val registrationEndpoint =
    Endpoint(RoutePattern.POST / "registration")
    .in[RegPerson]
    .out[Unit]
    .outHeader(token)
    .outHeader(HeaderCodec.name[String]("Access-Control-Expose-Headers"))
    .outErrors[ApplicationError](
      HttpCodec.error[HttpDatabaseError](Status.BadGateway),
      HttpCodec.error[HttpError](Status.BadRequest),
      HttpCodec.error[HttpValidationError](Status.BadRequest)
    )

  val acceptCodeEndpoint =
    Endpoint(RoutePattern.POST / "acceptCode")
    .in[AcceptCode]
    .header(token)
    .outErrors[ApplicationError](
      HttpCodec.error[HttpDatabaseError](Status.BadGateway),
      HttpCodec.error[HttpError](Status.BadRequest)
    )
    .out[PersonHttp]

  val authEndpoint = 
    Endpoint(RoutePattern.POST / "authorization")
    .in[AuthPerson]
    .header(token)
    .header(HeaderCodec.name[Int]("AuthType"))
    .out[Option[PersonHttp]]
    .outHeader(HeaderCodec.name[String]("Access-Control-Expose-Headers"))
    .outHeader(token)
    .outErrors[ApplicationError](
      HttpCodec.error[HttpDatabaseError](Status.BadGateway),
      HttpCodec.error[HttpError](Status.BadRequest),
      HttpCodec.error[HttpValidationError](Status.BadRequest)
    )

  val personInfoEndpoint = 
    Endpoint(RoutePattern.POST / "personInfo" )
      .in[UpdatePersonInfo]
      .header(token)
      .out[Unit]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest),
        HttpCodec.error[HttpNotFoundUser](Status.NotFound)
      )

  val passwordEndpoint =
    Endpoint(RoutePattern.POST / "password")
      .in[SetPassword]
      .header(token)
      .out[Unit]
      .outErrors[ApplicationError](
        HttpCodec.error[HttpNotFoundUser](Status.NotFound),
        HttpCodec.error[HttpDatabaseError](Status.BadGateway),
        HttpCodec.error[HttpError](Status.BadRequest)
      )

  val endpointList = List(
    registrationEndpoint, acceptCodeEndpoint, authEndpoint, personEndpoint, personInfoEndpoint, passwordEndpoint
  )
}
