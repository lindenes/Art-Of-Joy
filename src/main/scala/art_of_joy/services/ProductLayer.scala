package art_of_joy.services

import art_of_joy.ctx
import art_of_joy.model.product.{Product, ProductClientFilter}
import art_of_joy.services.interfaces.ProductService
import io.getquill.*
import zio.{ZIO, ZLayer}

import javax.sql.DataSource
object ProductLayer {
  import ctx._

  val live = ZLayer.succeed(
    new ProductService {
      override def getProduct(id: Long): ZIO[DataSource, Throwable, Product] = ???

      override def getProductList(filters:ProductClientFilter): ZIO[DataSource, Throwable, List[Product]] =
        ctx.run(
            dynamicQuery[Product]
              .filterOpt(filters.name.map(_ + "%"))((product, name) => quote( product.name.getOrElse("") like name))
              .filterOpt(filters.brandID.map(List(_)))((product, brand) => quote(brand.contains(product.brand_id)))
              .filterOpt(filters.subCategoryID.map(List(_)))((product, subCategory) => quote(subCategory.contains(product.subcategory_id)))
              .filterOpt(filters.maxPrice)((product, maxPrice) => quote(product.price.getOrElse(0.toDouble) <= maxPrice))
              .filterOpt(filters.minPrice)((product, minPrice) => quote(product.price.getOrElse(0.toDouble) >= minPrice))
        )
    }
  )
}
