package art_of_joy.application.model

import zio.schema.{Schema,DeriveSchema}
import java.sql.Timestamp

object Response {

  implicit val timestampSchema:Schema[Timestamp] = Schema[Long].transform(
    millis => new Timestamp(millis),
    ts => ts.getTime
  )

  case class SubCategoryHttp(id: Long, name: String, categoryId: Long)

  object SubCategoryHttp {
    implicit val schema: Schema[SubCategoryHttp] = DeriveSchema.gen
  }

  case class CategoryHttp(id: Long, name: String, subCategories: List[SubCategoryHttp])

  object CategoryHttp {
    implicit val schema: Schema[CategoryHttp] = DeriveSchema.gen
  }

  case class BrandHttp(id: Long, name: String)

  object BrandHttp {
    implicit val schema: Schema[BrandHttp] = DeriveSchema.gen
  }

  case class ProductHttp(id: Int,
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

  object ProductHttp {
    implicit val schema: Schema[ProductHttp] = DeriveSchema.gen
  }

  case class PersonHttp(
                         surname: Option[String],
                         email: String,
                         phone: Option[String],
                         role: Int,
                         firstname: Option[String],
                         middleName: Option[String],
                         id: Long,
                         havePassword: Boolean,
                         isConfirmEmail: Boolean,
                         isConfirmPhone: Boolean
                       )

  object PersonHttp {
    implicit val schema: Schema[PersonHttp] = DeriveSchema.gen
  }

  case class ExelProduct(
                          article: Option[String],
                          name: Option[String],
                          description: Option[String],
                          subcategoryName: Option[String],
                          categoryName:Option[String],
                          brandName: Option[String],
                          article_wb: Option[String],
                          barcode: Option[String],
                          material: Option[String],
                          fragility: Option[String],
                          product_country: Option[String],
                          color: Option[String],
                          height: Option[String],
                          width: Option[String],
                          size: Option[String],
                          ru_size: Option[String]
                        )
  object ExelProduct {
    implicit val schema: Schema[ExelProduct] = DeriveSchema.gen
  }
  
  case class CartProductHttp(
                            id:Long,
                            productName:String,
                            productId:Long,
                            count:Int,
                            price:Double
                            )

  object CartProductHttp {
    implicit val schema: Schema[CartProductHttp] = DeriveSchema.gen
  }
  
}
