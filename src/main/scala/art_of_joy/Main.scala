package art_of_joy
import io.getquill.*
import io.getquill.jdbczio.Quill.DataSource
import zio.*
import art_of_joy.config.ApplicationConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.http.netty.NettyConfig
import art_of_joy.application.http.getRoutes
import art_of_joy.repository.service.brand.BrandTableService
import art_of_joy.repository.service.category.CategoryTableService
import art_of_joy.repository.service.subcategory.SubCategoryTableService
import art_of_joy.domain.*
import art_of_joy.domain.service.category.CategoryService
import art_of_joy.domain.service.email.EmailService
import art_of_joy.domain.service.exel.ExelService
import art_of_joy.domain.service.person.PersonService
import art_of_joy.domain.service.session.{SessionStorage, SessionStorageService}
import art_of_joy.repository.service.product.ProductTableService
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
            inactiveUsers <- SessionStorage.checkInactivePersons
            _ <- SessionStorage.clearPersons(inactiveUsers)
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
        SessionStorageService.live,
        EmailService.live,
        PersonService.live,
        ExelService.live,
        ProductTableService.live,
        ZClient.default
      )
}
