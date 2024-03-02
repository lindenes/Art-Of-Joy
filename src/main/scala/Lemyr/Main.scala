import io.getquill.*
import io.getquill.jdbczio.Quill.DataSource
import zio.*
import com.typesafe.config.*
import lemyr.config.ApplicationConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.http.netty.NettyConfig
import lemyr.http.Route
import lemyr.services.interfaces.{CategoryTrait, SessionStorageTrait}
import lemyr.services.{CategoryLayer, SessionStorageLayer, UserLayer}
object Main extends ZIOAppDefault{
  
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    )

  override def run =
    (
      for{
        _ <- (
          for{
            service <- ZIO.service[SessionStorageTrait]
            inactiveUsers <- service.checkInactiveUsers
            _ <- service.clearUsers(inactiveUsers)
          }yield ()
          ).repeat(Schedule.spaced(15.minute)).forkDaemon
        _ <- Server.install(Route.getRoutes).map(port => println("Сервер запущен " + port)) *> ZIO.never
      }yield ExitCode.success
    )
      .provide(
        Scope.default,
        Server.customized,
        ApplicationConfig.getHttpConfig,
        ApplicationConfig.getNettyConfig,
        DataSource.fromPrefix("db"),
        UserLayer.live,
        CategoryLayer.live,
        SessionStorageLayer.live
      )
}



















//case class Category(id:Int, name:String)
//object Main extends ZIOAppDefault{
//
//  val ctx = new PostgresZioJdbcContext(SnakeCase)
//  import ctx._
//  override def run =
//    (for{
//      data <- ZIO.from(
//        quote{
//          query[Category].insert(_.name -> "тестовая4")
//        }
//      )
//      updatesql <- ZIO.from(
//        quote{
//          query[Category]
//        }
//      )
//      _ <- ctx.run(data)
//      sql <- ctx.run(updatesql)
//      _ <- Console.printLine(sql)
//    }yield "")
//      .provide(
//        DataSource.fromPrefix("db")
//      )
//}