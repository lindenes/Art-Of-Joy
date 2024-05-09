package art_of_joy
import io.getquill.*
import io.getquill.jdbczio.Quill.DataSource
import zio.*
import com.typesafe.config.*
import art_of_joy.config.ApplicationConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.http.netty.NettyConfig
import art_of_joy.http.getRoutes
import art_of_joy.services
import art_of_joy.services.interfaces.{CategoryTrait, SessionStorageTrait}
import art_of_joy.services.*
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
            inactiveUsers <- service.checkInactivePersons
            _ <- service.clearPersons(inactiveUsers)
          }yield ()
          ).repeat(Schedule.spaced(15.minute)).forkDaemon
        _ <- Server.install(getRoutes).map(port => println("Сервер запущен " + port)) *> ZIO.never
      }yield ExitCode.success
    )
      .provide(
        Scope.default,
        Server.customized,
        ApplicationConfig.getHttpConfig,
        ApplicationConfig.getNettyConfig,
        DataSource.fromPrefix("db"),
        PersonLayer.live,
        CategoryLayer.live,
        SessionStorageLayer.live,
        EmailServiceLayer.live,
        ExelLayer.live,
        ProductLayer.live
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