package art_of_joy.domain.service.exel
import art_of_joy.domain.model.ExelProduct
import zio.*
import zio.http.*
trait Exel {
  def getProductFromExel(data: Array[Byte]): ZIO[Client & Scope, Throwable, List[ExelProduct]]

  def loadImage(imageUrl: Array[String]): ZIO[Client & Scope, Throwable, Array[String]]
}
object Exel{
  def getProductFromExel(data: Array[Byte]) =
    ZIO.serviceWithZIO[Exel](_.getProductFromExel(data))

  def loadImage(imageUrl: Array[String]) =
    ZIO.serviceWithZIO[Exel](_.loadImage(imageUrl))
}
