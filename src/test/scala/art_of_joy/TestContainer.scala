package art_of_joy

import art_of_joy.application.getRoutes
import art_of_joy.application.model.Request.*
import art_of_joy.domain.model.{Person, StoragePerson}
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import art_of_joy.domain.service.session.SessionStorageService
import art_of_joy.repository.service.category.CategoryTableService
import art_of_joy.repository.service.person.PersonTableService
import art_of_joy.repository.service.product.ProductTableService
import art_of_joy.utils.Migration
import org.postgresql.ds.PGSimpleDataSource
import zio.*
import zio.http.*
import zio.http.netty.NettyConfig
import zio.http.netty.server.NettyDriver
import zio.test.*
import zio.test.TestAspect.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

import javax.sql.DataSource
object TestContainer extends ZIOSpecDefault{

  val postgreContainer:PostgreSQLContainer[_] = new PostgreSQLContainer(
    DockerImageName.parse("postgres:15.1"))

  val dataSourceLayer: Layer[Throwable, DataSource] =
    ZLayer.succeed {
      postgreContainer
        .withDatabaseName("test")
        .withUsername("admin")
        .withPassword("admin")
        .start()

      val config = new org.postgresql.ds.PGSimpleDataSource()
      config.setServerNames(Array("localhost"))
      config.setPortNumbers(Array(postgreContainer.getMappedPort(5432)))
      config.setDatabaseName("test")
      config.setUser("admin")
      config.setPassword("admin")
      config
    }

   def spec = suite("test container try")(
    test("add category") {
      for {
        client <- ZIO.service[Client]
        url <- ZIO.fromEither(URL.decode("http://127.0.0.1:9080/category"))
        _      <- TestServer.addRoutes {getRoutes}
        _ <- Migration.createTables
        addCategory <- client.request(
          Request.post(url, Body.from(List(CategoryAdd("testCategory", List("subTestCategory"))))).addHeader("token", "test")
        )
        body <- addCategory.body.asString
        _ <- Console.printLine(body)
      } yield assertTrue(true)
    }
  ).provide(
     dataSourceLayer,
     CategoryTableService.live,
     ApplicationConfig.smtpConfig,
     Scope.default,
     ZLayer{
       Ref.make(
         Map("test" -> StoragePerson(Person(None, "", None, Role.user, None, None, 1, None, true, true), 1L))
       ).map(SessionStorageService(_))
     },
     ProductTableService.live,
     PersonTableService.live,
     TestServer.layer,
     ZLayer.succeed(Server.Config.default.port(9080)),
     Client.default,
     NettyDriver.customized,
     ZLayer.succeed(NettyConfig.defaultWithFastShutdown),
  ) @@ after(ZIO.succeedBlocking(postgreContainer.stop()))

}
