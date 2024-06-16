package art_of_joy.services.interfaces
import art_of_joy.model.product.{ExelProduct, Product}
import zio.*
trait ExelService {
  def getProductFromExel(data:Array[Byte]): ZIO[http.Client & Scope, Throwable, List[ExelProduct]]
  def loadImage(imageUrl:Array[String]):ZIO[http.Client & Scope, Throwable, Array[String]]
}
