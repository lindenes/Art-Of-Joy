package art_of_joy

import art_of_joy.repository.model._
import io.getquill.*

package object repository {
  val categorySchema: Quoted[EntityQuery[CategoryRow]] =  quote(querySchema[CategoryRow]("category"))
  val subCategorySchema: Quoted[EntityQuery[SubCategoryRow]] = quote(querySchema[SubCategoryRow]("subcategory", _.categoryId -> "category_id"))
  val brandSchema: Quoted[EntityQuery[BrandRow]] = quote(querySchema[BrandRow]("brand"))
  val productSchema: Quoted[EntityQuery[ProductRow]] = quote(querySchema[ProductRow](
    "product",
    _.subcategoryId -> "subcategory_id",
    _.ruSize -> "ru_size",
    _.articleWb -> "article_wb",
    _.brandId -> "brand_id",
    _.productCountry -> "product_country",
    _.createdAt -> "created_at"
  ))
  val personSchema = quote(querySchema[PersonRow]("person", 
    _.isConfirmPhone -> "is_confirm_phone",
    _.isConfirmEmail -> "is_confirm_email", 
    _.middleName -> "middlename",
    _.passwordHash -> "password_hash",
    _.createdAt -> "created_at")
  )
  val productImageSchema = quote(querySchema[ProductImageRow]("product_image",
    _.productId -> "product_id",
    _.binaryData -> "binary_data"
  ))
  val cartSchema:Quoted[EntityQuery[CartRow]] = quote(querySchema[CartRow]("cargo", _.productId -> "product_id", _.personId -> "person_id"))
}
