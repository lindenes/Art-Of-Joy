package art_of_joy
import io.getquill.*
import io.getquill.jdbczio.Quill.DataSource
import zio.*
import art_of_joy.config.ApplicationConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.http.netty.NettyConfig
import art_of_joy.http.getRoutes
import art_of_joy.repository.brand.BrandTableService
import art_of_joy.repository.category.CategoryTableService
import art_of_joy.repository.subcategory.SubCategoryTableService
import art_of_joy.services.interfaces.SessionStorageService
import art_of_joy.services.*
import art_of_joy.services.category.CategoryService
import art_of_joy.utils.Migration
object Main extends ZIOAppDefault{
  
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(
      TypesafeConfigProvider
        .fromResourcePath()
    )

  override def run =
    (
      for{
        _ <- Migration.createTables
        _ <- (
          for{
            service <- ZIO.service[SessionStorageService]
            inactiveUsers <- service.checkInactivePersons
            _ <- service.clearPersons(inactiveUsers)
          }yield ()
          ).repeat(Schedule.spaced(15.minute)).forkDaemon
        _ <- Server.install(getRoutes).map(port => println("Сервер запущен " + port)) *> ZIO.never
      }yield ExitCode.success
    )
      .provide(
        CategoryService.live,
        CategoryTableService.live,
        SubCategoryTableService.live,
        BrandTableService.live,
        Scope.default,
        Server.customized,
        ApplicationConfig.getHttpConfig,
        ApplicationConfig.getNettyConfig,
        DataSource.fromPrefix("db"),
        PersonLayer.live,
        SessionStorageLayer.live,
        EmailServiceLayer.live,
        ExelLayer.live,
        ProductLayer.live,
        ZClient.default
      )
}
