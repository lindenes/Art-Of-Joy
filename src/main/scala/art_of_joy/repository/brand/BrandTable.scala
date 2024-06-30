package art_of_joy.repository.brand

import art_of_joy.model.category.BrandRow
import zio._
import javax.sql.DataSource

trait BrandTable {
  def getBrand:ZIO[DataSource, Throwable, List[BrandRow]]
}
object BrandTable{
  def getBrand:ZIO[DataSource & BrandTable, Throwable, List[BrandRow]] =
    ZIO.serviceWithZIO[BrandTable](_.getBrand)
}