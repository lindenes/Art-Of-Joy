package art_of_joy

import zio.*
import zio.config.*
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.http.*
import zio.http.Server.RequestStreaming
import zio.http.netty.NettyConfig

import java.net.InetSocketAddress
object ApplicationConfig {

  case class Api(host: String,threadSize:Int, port: Int, timeoutSec: Int, maxInitialLineLength:Int)
  case class SmtpConfig(host:String, port:String, username:String, password:String, auth:Boolean, startTls:Boolean, email:String)
  
  val httpConfig: ZLayer[Any, Config.Error, Server.Config] =
    ZLayer.fromZIO(
      TypesafeConfigProvider.fromResourcePath()
        .nested("api")
        .load(deriveConfig[Api])
        .map(config =>
          Server.Config.default
            .binding(config.host, config.port)
            .idleTimeout(config.timeoutSec.second)
            .maxInitialLineLength(config.maxInitialLineLength)
        )
    )
    
  val nettyConfig: ZLayer[Any, Config.Error, NettyConfig] =
    ZLayer.fromZIO(
      TypesafeConfigProvider.fromResourcePath()
        .nested("api")
        .load(deriveConfig[Api])
        .map(config => NettyConfig.default.maxThreads(config.threadSize))
    )
    
  val smtpConfig: ZLayer[Any, Config.Error, SmtpConfig] =
    ZLayer.fromZIO(
      TypesafeConfigProvider.fromResourcePath()
        .nested("SmtpConfig")
        .load(deriveConfig[SmtpConfig])
    )
}
