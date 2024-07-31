package art_of_joy.domain.service

import art_of_joy.application.model.Request.ProductClientFilter
import art_of_joy.repository.service.product.ProductTable

object ProductService {
  def getProducts(filter: ProductClientFilter) =
    ProductTable.getProductList(filter)
}
