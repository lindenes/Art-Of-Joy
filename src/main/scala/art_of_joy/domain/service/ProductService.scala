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
    cart <- ProductTable.getPersonCart(openStoragePerson.person.id)
    cartProductInfo <-
      ZIO.foreach(cart)(c => ProductTable.getProduct(c.productId)).map(_.flatten)
  }yield
    cartProductInfo.map(p =>{
        val c = cart.find(_.productId == p.id).get
        CartProduct(c.id, p.name, p.id, c.count, p.price * c.count)
      }
    )

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
