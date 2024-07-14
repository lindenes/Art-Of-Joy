package art_of_joy

import io.getquill.*
import io.getquill.jdbczio.Quill.DataSource
import zio.*
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.http.netty.NettyConfig
import art_of_joy.application.http.getRoutes
import art_of_joy.repository.service.category.CategoryTableService
import art_of_joy.domain.*
import art_of_joy.domain.service.exel.ExelService
import art_of_joy.domain.service.session.{SessionStorage, SessionStorageService}
import art_of_joy.repository.service.person.PersonTableService
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
        CategoryTableService.live,
        Scope.default,
        Server.customized,
        ApplicationConfig.getHttpConfig,
        ApplicationConfig.getNettyConfig,
        DataSource.fromPrefix("db"),
        SessionStorageService.live,
        ExelService.live,
        ProductTableService.live,
        PersonTableService.live,
        ZClient.default
      )
}
