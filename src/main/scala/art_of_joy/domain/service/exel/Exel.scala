package art_of_joy.domain.service.exel
import art_of_joy.domain.model.Errors.DomainError
import art_of_joy.domain.model.ExelProduct
import zio.*
import zio.http.*
trait Exel {
  def getProductFromExel(data: Array[Byte]): ZIO[Client & Scope, DomainError, List[ExelProduct]]

  def loadImage(imageUrl: Array[String]): ZIO[Client & Scope, DomainError, Array[String]]
}
object Exel{
  def getProductFromExel(data: Array[Byte]) =
    ZIO.serviceWithZIO[Exel](_.getProductFromExel(data))

  def loadImage(imageUrl: Array[String]) =
    ZIO.serviceWithZIO[Exel](_.loadImage(imageUrl))
}
