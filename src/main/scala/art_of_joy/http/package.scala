package art_of_joy
import zio.http.*
import zio.http.Middleware.{CorsConfig, cors}
import zio.http.Header.{AccessControlAllowHeaders, AccessControlAllowMethods, AccessControlAllowOrigin, AccessControlExposeHeaders, Origin}
package object http {

  val config: CorsConfig =
    CorsConfig(
      allowedOrigin = {
        case origin@Origin.Value(_, host, _) => Some(AccessControlAllowOrigin.Specific(origin))
        case origin@_ => Some(AccessControlAllowOrigin.Specific(origin))

      },
     // allowedMethods = AccessControlAllowMethods(Method.PUT, Method.POST, Method.GET, Method.DELETE),
    )
  def getRoutes = (CategoryRoute.getRoutes ++ PersonRoute.getRoutes ++ ProductRoute.getRoutes) @@ cors(config)
}
