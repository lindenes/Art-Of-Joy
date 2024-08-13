package art_of_joy.repository.service.product

import art_of_joy.application.model.Request.{ProductAdd, ProductClientFilter}
import art_of_joy.domain.model.Errors.DomainError

import javax.sql.DataSource
import zio.*
import art_of_joy.repository.model.ProductRow

trait ProductTable {
  def getProduct(id:Long): ZIO[DataSource, DomainError, List[ProductRow]]
  def getProductList(filters:ProductClientFilter):ZIO[DataSource, DomainError, List[ProductRow]]
  def addProduct(product:ProductAdd):ZIO[DataSource,DomainError, Long]
  def addPhoto(productId:Long, binaryData:Array[Byte]):ZIO[DataSource,DomainError, Long]
}
object ProductTable{
  def getProduct(id:Long) =
    ZIO.serviceWithZIO[ProductTable](_.getProduct(id))
  def getProductList(filters:ProductClientFilter) =
    ZIO.serviceWithZIO[ProductTable](_.getProductList(filters))
  def addProduct(product:ProductAdd) =
    ZIO.serviceWithZIO[ProductTable](_.addProduct(product))
  def addPhoto(productId:Long, binaryData:Array[Byte]) =
    ZIO.serviceWithZIO[ProductTable](_.addPhoto(productId, binaryData))
}
