package art_of_joy.model.product

import java.util.Calendar
import zio.json.*

import java.sql.Timestamp
case class Product(id:Int,
                   article:Option[String],
                   name:Option[String],
                   description:Option[String],
                   price:Option[Double],
                   subcategory_id:Option[Int],
                   brand_id:Option[Int],
                   created_at: Timestamp,
                   article_wb:Option[String],
                   barcode:Option[String],
                   material:Option[String],
                   fragility:Boolean,
                   product_country:Option[String],
                   color:Option[String],
                   height:Option[Double],
                   width:Option[Double],
                   size:Option[String],
                   ru_size:Option[String]
                  )
object Product{
  implicit val timestampCodec: JsonCodec[java.sql.Timestamp] = JsonCodec.apply[Timestamp](
    JsonEncoder.string.contramap(_.toString),
    JsonDecoder.string.map(java.sql.Timestamp.valueOf),
  )
  implicit val decoder: JsonDecoder[Product] = DeriveJsonDecoder.gen[Product]
  implicit val encoder: JsonEncoder[Product] = DeriveJsonEncoder.gen[Product]
}