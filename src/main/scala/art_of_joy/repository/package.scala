package art_of_joy

import art_of_joy.model.category.{BrandRow, CategoryRow, SubCategoryRow}
import io.getquill.*
package object repository {
  val categorySchema: Quoted[EntityQuery[CategoryRow]] =  quote(querySchema[CategoryRow]("category"))
  val subCategorySchema: Quoted[EntityQuery[SubCategoryRow]] = quote(querySchema[SubCategoryRow]("subcategory", _.categoryID -> "category_id"))
  val brandSchema: Quoted[EntityQuery[BrandRow]] = quote(querySchema[BrandRow]("brand"))
}
