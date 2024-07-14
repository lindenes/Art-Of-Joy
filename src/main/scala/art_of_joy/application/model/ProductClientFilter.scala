package art_of_joy.application.model

import zio.json._

case class ProductClientFilter(
                                subCategoryID:Option[Int], 
                                brandID:Option[Int],
                                maxPrice:Option[Double],
                                minPrice:Option[Double], 
                                name:Option[String]
                              )
object ProductClientFilter{
  implicit val decoder: JsonDecoder[ProductClientFilter] = DeriveJsonDecoder.gen[ProductClientFilter]
  implicit val encoder: JsonEncoder[ProductClientFilter] = DeriveJsonEncoder.gen[ProductClientFilter]
}