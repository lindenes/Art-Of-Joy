package art_of_joy.services.interfaces

import javax.sql.DataSource
import zio.*
import art_of_joy.model.product.{Product, ProductClientFilter}
trait ProductTrait {
  def getProduct(id:Long):ZIO[DataSource, Throwable, Product]
  def getProductList(filters:ProductClientFilter):ZIO[DataSource, Throwable, List[Product]]
}
