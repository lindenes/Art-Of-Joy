package art_of_joy.repository.model

import java.sql.Timestamp
import zio.json._
case class ProductRow(id:Int, 
                      article:Option[String],
                      name:Option[String],
                      description:Option[String],
                      price:Option[Double],
                      subcategoryID:Option[Int],
                      brandID:Option[Int],
                      createdAt: Timestamp,
                      articleWb:Option[String],
                      barcode:Option[String],
                      material:Option[String],
                      fragility:Boolean,
                      productCountry:Option[String],
                      color:Option[String],
                      height:Option[Double],
                      width:Option[Double],
                      size:Option[String],
                      ruSize:Option[String]
                     )
object ProductRow{
  implicit val timestampCodec: JsonCodec[java.sql.Timestamp] = JsonCodec.apply[Timestamp](
    JsonEncoder.string.contramap(_.toString),
    JsonDecoder.string.map(java.sql.Timestamp.valueOf),
  )
  implicit val decoder: JsonDecoder[ProductRow] = DeriveJsonDecoder.gen[ProductRow]
  implicit val encoder: JsonEncoder[ProductRow] = DeriveJsonEncoder.gen[ProductRow]
}