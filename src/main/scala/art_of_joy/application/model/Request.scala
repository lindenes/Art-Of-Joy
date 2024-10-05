package art_of_joy.application.model

import zio.json.*
import zio.schema.{Schema,DeriveSchema}

object Request {
  
  case class CategoryAdd(name: String, subNames: List[String])
  object CategoryAdd {
    implicit val schema: Schema[CategoryAdd] = DeriveSchema.gen
  }
  case class ProductClientFilter(
                                  subCategoryID: Option[Int],
                                  brandID: Option[Int],
                                  maxPrice: Option[Double],
                                  minPrice: Option[Double],
                                  name: Option[String]
                                )
  object ProductClientFilter {
    implicit val schema: Schema[ProductClientFilter] = DeriveSchema.gen
  }

  case class AuthPerson(email: Option[String], password: Option[String], phone: Option[String])
  object AuthPerson {
    implicit val schema: Schema[AuthPerson] = DeriveSchema.gen
  }
  
  case class RegPerson(email: Option[String], phone: Option[String])
  object RegPerson {
    implicit val schema: Schema[RegPerson] = DeriveSchema.gen
  }
  
  case class AcceptCode(acceptCode: String, acceptCodeType: Int)
  object AcceptCode {
    implicit val schema: Schema[AcceptCode] = DeriveSchema.gen
  }

  case class UpdatePersonInfo(surname: String,
                              firstname: String,
                              middleName: Option[String] = None)
  object UpdatePersonInfo {
    implicit val schema: Schema[UpdatePersonInfo] = DeriveSchema.gen
  }

  case class SetPassword(password: String, repeatPassword: String, oldPassword: Option[String])
  
  object SetPassword {
    implicit val schema: Schema[SetPassword] = DeriveSchema.gen
  }
  case class ExelBase64(exelData: String)
  object ExelBase64 {
    implicit val schema: Schema[ExelBase64] = DeriveSchema.gen
  }
  
  case class BrandAdd(name:String)
  object BrandAdd {
    implicit val schema: Schema[BrandAdd] = DeriveSchema.gen
  }
  
  case class ProductAdd(
                             article: String,
                             name: String,
                             description: Option[String],
                             subcategoryId: Long,
                             categoryId: Long,
                             brandId: Long,
                             articleWb: Option[String],
                             barcode: String,
                             material: Option[String],
                             fragility: Option[Boolean],
                             productCountry: Option[String],
                             color: Option[String],
                             height: Option[String],
                             width: Option[String],
                             size: Option[String],
                             ruSize: Option[String]
                           )

  object ProductAdd {
    implicit val schema: Schema[ProductAdd] = DeriveSchema.gen
  }
  
  case class ProductPhotoAdd(id:Long, binaryData:List[Byte])

  object ProductPhotoAdd {
    implicit val schema: Schema[ProductPhotoAdd] = DeriveSchema.gen
  }
  
  case class AddToCart(productId:Long)
  object AddToCart {
    implicit val schema: Schema[AddToCart] = DeriveSchema.gen
  }

  case class DeleteFromCart(id: Long)
  object DeleteFromCart {
    implicit val schema: Schema[DeleteFromCart] = DeriveSchema.gen
  }
}
