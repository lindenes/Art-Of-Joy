package art_of_joy.application

import sttp.apispec.openapi.Info
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import zio.Task
import zio.http.*
import zio.http.Middleware.{CorsConfig, cors}
import zio.http.Header.{AccessControlAllowHeaders, AccessControlAllowMethods, AccessControlAllowOrigin, AccessControlExposeHeaders, Origin}
import zio.http.endpoint.openapi.SwaggerUI
package object http {

  val config: CorsConfig =
    CorsConfig(
      allowedOrigin = {
        case origin@Origin.Value(_, host, _) => Some(AccessControlAllowOrigin.Specific(origin))
        case origin@_ => Some(AccessControlAllowOrigin.Specific(origin))

      },
     // allowedMethods = AccessControlAllowMethods(Method.PUT, Method.POST, Method.GET, Method.DELETE),
    )

  def getSwaggerDocRoutes = SwaggerInterpreter().fromEndpoints[Task](
    ProductRoute.endPointList ++ PersonRoute.endPointList ++ CategoryRoute.endPointList,
    Info("Документация по api", "0.0.1")
  )
  def getRoutes = (CategoryRoute.routes ++ PersonRoute.routes ++ ProductRoute.routes) @@ cors(config) ++ ZioHttpInterpreter().toHttp(getSwaggerDocRoutes)
}
