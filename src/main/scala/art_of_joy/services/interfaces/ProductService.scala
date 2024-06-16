package art_of_joy.services.interfaces

import javax.sql.DataSource
import zio.*
import art_of_joy.model.product.{Product, ProductClientFilter}
trait ProductService {
  def getProduct(id:Long):ZIO[DataSource, Throwable, Product]
  def getProductList(filters:ProductClientFilter):ZIO[DataSource, Throwable, List[Product]]
}
object ProductService{
  def getProduct(id:Long) =
    ZIO.serviceWithZIO[ProductService](_.getProduct(id))
  def getProductList(filters:ProductClientFilter) =
    ZIO.serviceWithZIO[ProductService](_.getProductList(filters))
}