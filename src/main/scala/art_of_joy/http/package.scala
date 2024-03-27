package art_of_joy
import zio.http._
import zio.http.Middleware.{CorsConfig, cors}
import zio.http.Header.{AccessControlAllowMethods, AccessControlAllowOrigin, Origin}
package object http {

  val config: CorsConfig =
    CorsConfig(
      allowedOrigin = {
        case origin@Origin.Value(_, host, _) => Some(AccessControlAllowOrigin.Specific(origin))
        case origin@_ => Some(AccessControlAllowOrigin.Specific(origin))

      },
      allowedMethods = AccessControlAllowMethods(Method.PUT, Method.POST, Method.GET, Method.DELETE),
    )
  def getRoutes = (CategoryRoute.getRoutes ++ PersonRoute.getRoutes ++ ProductRoute.getRoutes) @@ cors(config)
}
