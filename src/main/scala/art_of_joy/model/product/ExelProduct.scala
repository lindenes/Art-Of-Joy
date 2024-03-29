package art_of_joy.model.product

import zio.json._

import java.util.Calendar

case class ExelProduct(
                        article:Option[String],
                        name:Option[String],
                        description:Option[String],
                        subcategoryName:Option[String],
                        brandName:Option[String],
                        article_wb:Option[String],
                        barcode:Option[String],
                        material:Option[String],
                        fragility:Option[String],
                        product_country:Option[String],
                        color:Option[String],
                        height:Option[String],
                        width:Option[String],
                        size:Option[String],
                        ru_size:Option[String],
                        mediaFile:Option[String]
                      )
object ExelProduct{
  implicit val encoder: JsonEncoder[ExelProduct] = DeriveJsonEncoder.gen[ExelProduct]
}