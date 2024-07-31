package art_of_joy.application.http

import art_of_joy.Env
import art_of_joy.application.model.Errors.*
import art_of_joy._
import zio.{Scope, ZIO}
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import art_of_joy.utils.*
import zio.json.ast.{Json, JsonCursor}
import art_of_joy.application.model.Request._
import art_of_joy.application.model.Response._
import sttp.model.StatusCode
import sttp.tapir.EndpointOutput

import java.util.{Date, UUID}
import javax.sql.DataSource

object PersonRoute {
  val baseEndpoint = endpoint
    .errorOut(
      oneOf[ApplicationError](
        oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[HttpValidationError])),
        oneOfVariant(statusCode(StatusCode.BadGateway).and(jsonBody[HttpDatabaseError])),
        oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[HttpError]))
      )
    )

  val personEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] = endpoint.get
    .in("person")
    .in(token)
    .in(query[Int]("startRow"))
    .in(query[Option[Int]]("endRow"))
    .out(jsonBody[List[PersonHttp]])
    .errorOut(
      oneOf[ApplicationError](
        oneOfVariant(statusCode(StatusCode.BadGateway).and(jsonBody[HttpDatabaseError])),
        oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[HttpError]))
      )
    )
    .zServerLogic((token, startRow, endRow) => AppHandler.getUserList(token, startRow, endRow))

  val registrationEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] = baseEndpoint.post
    .in("registration")
    .in(jsonBody[RegPerson])
    .out(header[String]("Token"))
    .out(header[String]("Access-Control-Expose-Headers"))
    .zServerLogic(AppHandler.registration)

  val acceptCodeEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] = endpoint.post
    .in("acceptCode")
    .in(jsonBody[AcceptCode])
    .in(token)
    .errorOut(
      oneOf[ApplicationError](
        oneOfVariant(statusCode(StatusCode.BadGateway).and(jsonBody[HttpDatabaseError])),
        oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[HttpError]))
      )
    )
    .out(jsonBody[PersonHttp])
    .zServerLogic((acceptCode, token) => AppHandler.checkAcceptCode(acceptCode, token))

  val authEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] = baseEndpoint.post
    .in("authorization")
    .in(jsonBody[AuthPerson])
    .securityIn(header[Option[String]]("Token"))
    .securityIn(header[Int]("AuthType"))
    .out(jsonBody[Option[PersonHttp]])
    .out(header[String]("Access-Control-Expose-Headers"))
    .out(header[String]("Token"))
    .zServerSecurityLogic((token, authType) => AppHandler.checkForTokenAuth(token, authType))
    .serverLogic((token, authType) => clientPerson => AppHandler.authorization(clientPerson, token, authType))

  val personInfoEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] = baseEndpoint.post
    .in("personInfo")
    .in(jsonBody[UpdatePersonInfo])
    .in(token)
    .zServerLogic((updatePersonInfo, token) => AppHandler.setPersonInfo(updatePersonInfo, token))

  val passwordEndpoint: ZServerEndpoint[Env & Scope & DataSource, Any] = baseEndpoint.post
    .in("password")
    .in(jsonBody[SetPassword])
    .in(token)
    .zServerLogic((setPassword,token) => AppHandler.setPassword(setPassword,token))

  val allEndpoints = List(
    personEndpoint, registrationEndpoint, authEndpoint, acceptCodeEndpoint, personInfoEndpoint, passwordEndpoint
  )
  val routes = ZioHttpInterpreter().toHttp(allEndpoints)
  val endPointList = allEndpoints.map(_.endpoint)
}
