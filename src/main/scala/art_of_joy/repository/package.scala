package art_of_joy

import art_of_joy.model.category.BrandRow
import art_of_joy.repository.model.{CategoryRow, ProductRow, SubCategoryRow}
import io.getquill.*
package object repository {
  val categorySchema: Quoted[EntityQuery[CategoryRow]] =  quote(querySchema[CategoryRow]("category"))
  val subCategorySchema: Quoted[EntityQuery[SubCategoryRow]] = quote(querySchema[SubCategoryRow]("subcategory", _.categoryID -> "category_id"))
  val brandSchema: Quoted[EntityQuery[BrandRow]] = quote(querySchema[BrandRow]("brand"))
  val productSchema: Quoted[EntityQuery[ProductRow]] = quote(querySchema[ProductRow](
    "product",
    _.subcategoryID -> "subcategory_id",
    _.ruSize -> "ru_size",
    _.articleWb -> "article_wb",
    _.brandID -> "brand_id",
    _.productCountry -> "product_country",
    _.createdAt -> "created_at"
  ))
}
