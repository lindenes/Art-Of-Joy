package art_of_joy.config

import zio.*
import zio.http.*
import zio.config.*
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.Server.RequestStreaming
import zio.http.netty.NettyConfig

import java.net.InetSocketAddress
object ApplicationConfig {

  case class ApiEndPoint(host: String,threadSize:Int, port: Int, timeoutSec: Int, maxInitialLineLength:Int)

  private val appConfig = deriveConfig[ApiEndPoint].nested("api")

//  def getHttpConfig2 = read(
//    appConfig from TypesafeConfigProvider.fromTypesafeConfig(com.typesafe.config.ConfigFactory.load())
//  )
  def getHttpConfig =
    ZLayer.fromZIO(
      ZIO.config[ApiEndPoint](appConfig).map{data =>
        Server.Config(
          sslConfig = None,
          address = new InetSocketAddress(data.host, data.port),
          acceptContinue = false,
          keepAlive = true,
          requestDecompression = Decompression.No,
          responseCompression = None,
          requestStreaming = RequestStreaming.Disabled(1024 * 100),
          maxHeaderSize = 8192,
          logWarningOnFatalError = true,
          gracefulShutdownTimeout = data.timeoutSec.second,
          webSocketConfig = WebSocketConfig.default,
          idleTimeout = None,
          maxInitialLineLength = data.maxInitialLineLength
        )
      }
    )
  def getNettyConfig =
    ZLayer.fromZIO(
      ZIO.config[ApiEndPoint](appConfig).map{data => 
        NettyConfig.default.maxThreads(data.threadSize)
      }
    )
}
