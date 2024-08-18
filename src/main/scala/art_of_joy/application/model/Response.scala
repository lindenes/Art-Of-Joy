package art_of_joy.application.model

import zio.json.*

import java.sql.Timestamp

object Response {
  case class SubCategoryHttp(id: Long, name: String, categoryId: Long)

  object SubCategoryHttp {
    implicit val decoder: JsonDecoder[SubCategoryHttp] = DeriveJsonDecoder.gen[SubCategoryHttp]
    implicit val encoder: JsonEncoder[SubCategoryHttp] = DeriveJsonEncoder.gen[SubCategoryHttp]
  }

  case class CategoryHttp(id: Long, name: String, subCategories: List[SubCategoryHttp])

  object CategoryHttp {
    implicit val decoder: JsonDecoder[CategoryHttp] = DeriveJsonDecoder.gen[CategoryHttp]
    implicit val encoder: JsonEncoder[CategoryHttp] = DeriveJsonEncoder.gen[CategoryHttp]
  }

  case class BrandHttp(id: Long, name: String)

  object BrandHttp {
    implicit val decoder: JsonDecoder[BrandHttp] = DeriveJsonDecoder.gen[BrandHttp]
    implicit val encoder: JsonEncoder[BrandHttp] = DeriveJsonEncoder.gen[BrandHttp]
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
    implicit val timestampCodec: JsonCodec[Timestamp] = JsonCodec.apply[Timestamp](
      JsonEncoder.string.contramap(_.toString),
      JsonDecoder.string.map(Timestamp.valueOf),
    )
    implicit val decoder: JsonDecoder[ProductHttp] = DeriveJsonDecoder.gen[ProductHttp]
    implicit val encoder: JsonEncoder[ProductHttp] = DeriveJsonEncoder.gen[ProductHttp]
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
    implicit val decoder: JsonDecoder[PersonHttp] = DeriveJsonDecoder.gen[PersonHttp]
    implicit val encoder: JsonEncoder[PersonHttp] = DeriveJsonEncoder.gen[PersonHttp]
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
    implicit val decoder: JsonDecoder[ExelProduct] = DeriveJsonDecoder.gen[ExelProduct]
    implicit val encoder: JsonEncoder[ExelProduct] = DeriveJsonEncoder.gen[ExelProduct]
  }
  
}
