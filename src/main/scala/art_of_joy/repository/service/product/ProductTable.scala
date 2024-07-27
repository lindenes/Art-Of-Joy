package art_of_joy.repository.service.product

import art_of_joy.application.model.ProductClientFilter
import art_of_joy.domain.model.Errors.DomainError

import javax.sql.DataSource
import zio.*
import art_of_joy.repository.model.ProductRow

trait ProductTable {
  def getProduct(id:Long): ZIO[DataSource, DomainError, List[ProductRow]]
  def getProductList(filters:ProductClientFilter):ZIO[DataSource, DomainError, List[ProductRow]]
}
object ProductTable{
  def getProduct(id:Long) =
    ZIO.serviceWithZIO[ProductTable](_.getProduct(id))
  def getProductList(filters:ProductClientFilter) =
    ZIO.serviceWithZIO[ProductTable](_.getProductList(filters))
}
