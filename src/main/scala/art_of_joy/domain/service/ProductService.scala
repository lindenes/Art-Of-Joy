package art_of_joy.domain.service

import art_of_joy.application.model.Request.{ProductAdd, ProductClientFilter}
import art_of_joy.repository.service.product.ProductTable

object ProductService {
  def getProducts(filter: ProductClientFilter) =
    ProductTable.getProductList(filter)
  
  def addProduct(product:ProductAdd) =
    ProductTable.addProduct(product)
  
  def addPhoto(productId:Long, binaryData:Array[Byte]) =
    ProductTable.addPhoto(productId, binaryData)
}
