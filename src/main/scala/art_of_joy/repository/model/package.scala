package art_of_joy.repository

import java.sql.Timestamp

package object model {
  
  case class SubCategoryRow(id: Long, name: String, categoryId: Long)

  case class ProductRow(id: Int,
                        article: String,
                        name: String,
                        description: Option[String],
                        price: Double,
                        subcategoryId: Long,
                        brandId: Long,
                        createdAt: Timestamp,
                        articleWb: Option[String],
                        barcode: String,
                        material: Option[String],
                        fragility: Boolean,
                        productCountry: Option[String],
                        color: Option[String],
                        height: Option[Double],
                        width: Option[Double],
                        size: Option[String],
                        ruSize: Option[String]
                       )

  case class PersonRow(surname: Option[String],
                       email: String,
                       phone: Option[String],
                       role: Int,
                       firstname: Option[String],
                       middleName: Option[String],
                       id: Long,
                       passwordHash: Option[String],
                       isConfirmEmail: Boolean,
                       isConfirmPhone: Boolean,
                       createdAt: Timestamp
                      )
  
  case class CategoryRow(id:Long, name:String)

  case class BrandRow(id:Long, name:String)
  
  case class ProductImageRow(id:Long, binaryData:Array[Byte], productId:Long)

  case class CartRow(id:Long, productId:Long, personId:Long, count:Int)
}
