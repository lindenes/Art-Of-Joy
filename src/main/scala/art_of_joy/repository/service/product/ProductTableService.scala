package art_of_joy.repository.service.product

import art_of_joy.application.model.ProductClientFilter
import art_of_joy.ctx
import art_of_joy.domain.model.Errors.{DataBaseError, DomainError}
import art_of_joy.repository.model.ProductRow
import art_of_joy.repository.productSchema
import art_of_joy.repository.service.product
import zio.*
import io.getquill.*

import javax.sql.DataSource

class ProductTableService extends ProductTable {
  import ctx._
  override def getProduct(id: Long): ZIO[DataSource, DomainError, List[ProductRow]] = 
    ctx.run(productSchema.filter(_.id == lift(id)))
      .mapError(ex => DataBaseError(exception = ex))

  override def getProductList(filters: ProductClientFilter): ZIO[DataSource, DomainError, List[ProductRow]] = ctx.run(
    dynamicQuerySchema[ProductRow](
      "product",
      alias(_.subcategoryId, "subcategory_id"),
      alias(_.ruSize, "ru_size"),
      alias(_.articleWb, "article_wb"),
      alias(_.brandID, "brand_id"),
      alias(_.productCountry, "product_country"),
      alias(_.createdAt, "created_at")
    )
      .filterOpt(filters.name.map(_ + "%"))((product, name) => quote( product.name.getOrElse("") like name))
      .filterOpt(filters.brandID.map(List(_)))((product, brand) => quote(brand.contains(product.brandID)))
      .filterOpt(filters.subCategoryID.map(List(_)))((product, subCategory) => quote(subCategory.contains(product.subcategoryId)))
      .filterOpt(filters.maxPrice)((product, maxPrice) => quote(product.price.getOrElse(0.toDouble) <= maxPrice))
      .filterOpt(filters.minPrice)((product, minPrice) => quote(product.price.getOrElse(0.toDouble) >= minPrice))
  ).mapError(ex => DataBaseError(exception = ex))
}
object ProductTableService{
  val live = ZLayer.succeed(ProductTableService())
}