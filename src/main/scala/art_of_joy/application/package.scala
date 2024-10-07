package art_of_joy

import zio.http.*
import zio.http.codec.PathCodec.*
import art_of_joy.application.http.*
import art_of_joy.application.service.RouteImpl
import zio.http.Header.{AccessControlAllowOrigin, Origin}
import zio.http.Middleware.{CorsConfig, cors}
import zio.http.endpoint.openapi.*

package object application {

  val config: CorsConfig =
    CorsConfig(
      allowedOrigin = {
        case origin@Origin.Value(_, host, _) => Some(AccessControlAllowOrigin.Specific(origin))
        case origin@_ => Some(AccessControlAllowOrigin.Specific(origin))

      },
      // allowedMethods = AccessControlAllowMethods(Method.PUT, Method.POST, Method.GET, Method.DELETE),
    )

  val swaggerRoutes = SwaggerUI.routes(
    "docs",
    OpenAPIGen.fromEndpoints(
      title = "Kaban_Zdorova",
      version = "1.0",
      CategoryEndpoint.endpointList ::: PersonEndpoint.endpointList ::: ProductEndpoint.endpointList
    )
  )

  def getRoutes = 
    (
      Routes.fromIterable(
        RouteImpl.categoryImpl ::: RouteImpl.productImpl ::: RouteImpl.personImpl
      ) ++ swaggerRoutes
    ) @@ cors(config)

}
