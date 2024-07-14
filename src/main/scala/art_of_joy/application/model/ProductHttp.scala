package art_of_joy.application.model
import zio.json.*

import java.sql.Timestamp
case class ProductHttp(id: Int,
                       article: Option[String],
                       name: Option[String],
                       description: Option[String],
                       price: Option[Double],
                       subcategory_id: Option[Int],
                       brand_id: Option[Int],
                       created_at: Timestamp,
                       article_wb: Option[String],
                       barcode: Option[String],
                       material: Option[String],
                       fragility: Boolean,
                       product_country: Option[String],
                       color: Option[String],
                       height: Option[Double],
                       width: Option[Double],
                       size: Option[String],
                       ru_size: Option[String]
                      )

object ProductHttp {
  implicit val timestampCodec: JsonCodec[java.sql.Timestamp] = JsonCodec.apply[Timestamp](
    JsonEncoder.string.contramap(_.toString),
    JsonDecoder.string.map(java.sql.Timestamp.valueOf),
  )
  implicit val decoder: JsonDecoder[ProductHttp] = DeriveJsonDecoder.gen[ProductHttp]
  implicit val encoder: JsonEncoder[ProductHttp] = DeriveJsonEncoder.gen[ProductHttp]
}
