package art_of_joy.repository.service.product

import art_of_joy.application.model.ProductClientFilter

import javax.sql.DataSource
import zio.*
import art_of_joy.repository.model.ProductRow

trait ProductTable {
  def getProduct(id:Long): ZIO[DataSource, Throwable, List[ProductRow]]
  def getProductList(filters:ProductClientFilter):ZIO[DataSource, Throwable, List[ProductRow]]
}
object ProductTable{
  def getProduct(id:Long): ZIO[DataSource & ProductTable, Throwable, List[ProductRow]] =
    ZIO.serviceWithZIO[ProductTable](_.getProduct(id))
  def getProductList(filters:ProductClientFilter): ZIO[DataSource & ProductTable, Throwable, List[ProductRow]] =
    ZIO.serviceWithZIO[ProductTable](_.getProductList(filters))
}
