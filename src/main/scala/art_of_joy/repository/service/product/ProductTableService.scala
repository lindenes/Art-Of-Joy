package art_of_joy.repository.service.product

import art_of_joy.application.model.Request
import art_of_joy.application.model.Request.{ProductAdd, ProductClientFilter}
import art_of_joy.ctx
import art_of_joy.domain.model.Errors.{DataBaseError, DomainError}
import art_of_joy.repository.model.{ProductRow, CartRow}
import art_of_joy.repository.*
import art_of_joy.repository.service.product
import zio.*
import io.getquill._

import javax.sql.DataSource

class ProductTableService extends ProductTable {
  import ctx._
  override def getProduct(id: Long): ZIO[DataSource, DomainError, List[ProductRow]] = 
    ctx.run(productSchema.filter(_.id == lift(id)))
      .mapError(ex => DataBaseError())

  override def getProductList(filters: ProductClientFilter): ZIO[DataSource, DomainError, List[ProductRow]] = ctx.run(
    dynamicQuerySchema[ProductRow](
      "product",
      alias(_.subcategoryId, "subcategory_id"),
      alias(_.ruSize, "ru_size"),
      alias(_.articleWb, "article_wb"),
      alias(_.brandId, "brand_id"),
      alias(_.productCountry, "product_country"),
      alias(_.createdAt, "created_at")
    )
      .filterOpt(filters.name.map(_ + "%"))((product, name) => quote( product.name like name))
      .filterOpt(filters.brandID.map(List(_)))((product, brand) => quote(brand.contains(product.brandId)))
      .filterOpt(filters.subCategoryID.map(List(_)))((product, subCategory) => quote(subCategory.contains(product.subcategoryId)))
      .filterOpt(filters.maxPrice)((product, maxPrice) => quote(product.price <= maxPrice))
      .filterOpt(filters.minPrice)((product, minPrice) => quote(product.price >= minPrice))
  ).mapError(ex => DataBaseError())

  override def addProduct(product: ProductAdd): ZIO[DataSource, DomainError, Long] =
    ctx.run(
      dynamicQuerySchema[ProductAdd](
        "product",
        alias(_.subcategoryId, "subcategory_id"),
        alias(_.ruSize, "ru_size"),
        alias(_.articleWb, "article_wb"),
        alias(_.brandId, "brand_id"),
        alias(_.productCountry, "product_country")
      )
        .insert(
          setValue(_.subcategoryId, product.subcategoryId),
          setValue(_.name, product.name),
          setValue(_.article, product.article),
          setValue(_.categoryId, product.categoryId),
          setValue(_.brandId, product.brandId),
          setValue(_.barcode, product.barcode),
          setOpt(_.description.getOrElse(""), product.description),
          setOpt(_.articleWb.getOrElse(""), product.articleWb),
          setOpt(_.material.getOrElse(""), product.material),
          setOpt(_.fragility.getOrElse(false), product.fragility),
          setOpt(_.productCountry.getOrElse(""), product.productCountry),
          setOpt(_.color.getOrElse(""), product.color),
          setOpt(_.height.getOrElse(""), product.height),
          setOpt(_.width.getOrElse(""), product.width),
          setOpt(_.size.getOrElse(""), product.size),
          setOpt(_.ruSize.getOrElse(""), product.ruSize)
        )
    ).mapError(ex => DataBaseError())

  override def addPhoto(productId: Long, binaryData: Array[Byte]): ZIO[DataSource, DomainError, Long] =
    ctx.run(
      productImageSchema.insert(_.productId -> lift(productId), _.binaryData -> lift(binaryData))
    ).mapError(ex => DataBaseError())

  override def addToCart(productId:Long, personId:Long):ZIO[DataSource, DomainError, Long] =
    ctx.run(
      cartSchema.insert(_.productId -> lift(productId), _.personId -> lift(personId)).returning(_.id)
    ).mapError(ex => DataBaseError())

  override def getPersonCart(personId: Long): ZIO[DataSource, DomainError, List[(CartRow, ProductRow)]] =
    ctx.run(
      cartSchema.filter(_.personId == lift(personId)).join(query[ProductRow]).on(_.productId == _.id)
    ).mapError(ex => DataBaseError())

  override def deleteProductFromCart(personId:Long, productId:Long) =
    ctx.run(
      cartSchema.filter(p => p.personId == lift(personId) && p.productId == lift(productId)).delete.returning(_.id)
    ).mapError(ex => DataBaseError())
}
object ProductTableService{
  val live = ZLayer.succeed(ProductTableService())
}
