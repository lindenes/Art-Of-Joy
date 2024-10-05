package art_of_joy.repository.service.product

import art_of_joy.application.model.Request.{ProductAdd, ProductClientFilter}
import art_of_joy.domain.model.Errors.DomainError

import javax.sql.DataSource
import zio.*
import art_of_joy.repository.model.{ProductRow, CartRow}

trait ProductTable {
  def getProduct(id:Long): ZIO[DataSource, DomainError, List[ProductRow]]
  def getProductList(filters:ProductClientFilter):ZIO[DataSource, DomainError, List[ProductRow]]
  def addProduct(product:ProductAdd):ZIO[DataSource,DomainError, Long]
  def addPhoto(productId:Long, binaryData:Array[Byte]):ZIO[DataSource,DomainError, Long]
  def addToCart(productId:Long, personId:Long):ZIO[DataSource, DomainError, Long]
  def getPersonCart(personId:Long):ZIO[DataSource, DomainError, List[(CartRow, ProductRow)]]
  def deleteProductFromCart(personId:Long, productId:Long):ZIO[DataSource, DomainError, Long]
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
  def addToCart(productId:Long, personId:Long) =
    ZIO.serviceWithZIO[ProductTable](_.addToCart(productId, personId))
  def getPersonCart(personId:Long) =
    ZIO.serviceWithZIO[ProductTable](_.getPersonCart(personId))
  def deleteProductFromCart(personId:Long, productId:Long) =
    ZIO.serviceWithZIO[ProductTable](_.deleteProductFromCart(personId, productId))
}
