package art_of_joy

import io.getquill.*
import io.getquill.jdbczio.Quill.DataSource
import zio.*
import zio.http.*
import zio.http.netty.NettyConfig
import art_of_joy.application.getRoutes
import art_of_joy.repository.service.category.CategoryTableService
import art_of_joy.domain.*
import art_of_joy.domain.service.session.{SessionStorage, SessionStorageService}
import art_of_joy.repository.service.person.PersonTableService
import art_of_joy.repository.service.product.ProductTableService
import art_of_joy.utils.Migration
import zio.logging.LogFilter.LogLevelByNameConfig
import zio.logging.{ConsoleLoggerConfig, LogFormat, consoleJsonLogger, fileAsyncJsonLogger}

object Main extends ZIOAppDefault{

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = Runtime.removeDefaultLoggers >>> 
    consoleJsonLogger(ConsoleLoggerConfig(LogFormat.default |-| LogFormat.allAnnotations, LogLevelByNameConfig.default))

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
        _ <- Server.install(getRoutes).flatMap(port => ZIO.log("server start on port " + port)) *> ZIO.never
      }yield ExitCode.success
    )
      .provide(
        CategoryTableService.live,
        Scope.default,
        Server.customized,
        ApplicationConfig.httpConfig,
        ApplicationConfig.nettyConfig,
        ApplicationConfig.smtpConfig,
        DataSource.fromPrefix("db"),
        SessionStorageService.live,
        ProductTableService.live,
        PersonTableService.live
      )
}
