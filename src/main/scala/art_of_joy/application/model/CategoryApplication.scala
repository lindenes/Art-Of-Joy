package art_of_joy.application.model
import zio.json._
object CategoryApplication {
  
  case class SubCategoryHttp(id:Long, name:String, categoryId:Long)
  object SubCategoryHttp {
    implicit val decoder: JsonDecoder[SubCategoryHttp] = DeriveJsonDecoder.gen[SubCategoryHttp]
    implicit val encoder: JsonEncoder[SubCategoryHttp] = DeriveJsonEncoder.gen[SubCategoryHttp]
  }
  
  case class CategoryHttp(id:Long, name:String, subCategories:List[SubCategoryHttp])
  object CategoryHttp {
    implicit val decoder: JsonDecoder[CategoryHttp] = DeriveJsonDecoder.gen[CategoryHttp]
    implicit val encoder: JsonEncoder[CategoryHttp] = DeriveJsonEncoder.gen[CategoryHttp]
  }

  case class CategoryAdd(name: String, subNames: List[String])
  object CategoryAdd {
    implicit val decoder: JsonDecoder[CategoryAdd] = DeriveJsonDecoder.gen[CategoryAdd]
    implicit val encoder: JsonEncoder[CategoryAdd] = DeriveJsonEncoder.gen[CategoryAdd]
  }
  
  case class BrandHttp(id:Long,name:String)
  object BrandHttp {
    implicit val decoder: JsonDecoder[BrandHttp] = DeriveJsonDecoder.gen[BrandHttp]
    implicit val encoder: JsonEncoder[BrandHttp] = DeriveJsonEncoder.gen[BrandHttp]
  }
}
