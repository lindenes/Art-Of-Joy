package art_of_joy.domain.service

import art_of_joy.application.model.Request.{ProductAdd, ProductClientFilter}
import art_of_joy.domain.model.CartProduct
import art_of_joy.domain.model.Errors.StorageError
import art_of_joy.domain.service.session.SessionStorage
import art_of_joy.repository.service.product.ProductTable
import zio.*

object ProductService {
  def getProducts(filter: ProductClientFilter) =
    ProductTable.getProductList(filter)
  
  def addProduct(product:ProductAdd) =
    ProductTable.addProduct(product)
  
  def addPhoto(productId:Long, binaryData:Array[Byte]) =
    ProductTable.addPhoto(productId, binaryData)

  def getPersonCart(token:String) = for{
    storagePerson <- SessionStorage.get(token)
    openStoragePerson <- ZIO.fromOption(storagePerson).orElseFail(StorageError())
    cartProduct <- ProductTable.getPersonCart(openStoragePerson.person.id)
  }yield cartProduct.map{case (cart, product) => CartProduct(cart.id, product.name, product.id, cart.count, cart.count * product.price)}

  def addToCart(productId:Long, token:String) = for{
    storagePerson <- SessionStorage.get(token)
    openStoragePerson <- ZIO.fromOption(storagePerson).orElseFail(StorageError())
    added <- ProductTable.addToCart(productId, openStoragePerson.person.id)
  }yield added
  
  def deleteFromCart(token:String, productId:Long) = for{
    storagePerson <- SessionStorage.get(token)
    openStoragePerson <- ZIO.fromOption(storagePerson).orElseFail(StorageError())
    deleted <- ProductTable.deleteProductFromCart(openStoragePerson.person.id, productId)
  }yield deleted
}
