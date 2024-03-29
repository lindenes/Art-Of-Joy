package art_of_joy.services.interfaces
import art_of_joy.model.product.{ExelProduct, Product}
import zio.*
trait ExelTrait {
  def getProductFromExel(data:Array[Byte]): ZIO[Any, Throwable, List[ExelProduct]]
}
