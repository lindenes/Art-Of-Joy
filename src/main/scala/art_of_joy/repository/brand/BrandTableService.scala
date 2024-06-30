package art_of_joy.repository.brand
import art_of_joy.model.category.BrandRow
import zio.*
import art_of_joy.ctx
import art_of_joy.repository.brandSchema

import javax.sql.DataSource
class BrandTableService extends BrandTable {
  import ctx._
  override def getBrand: ZIO[DataSource, Throwable, List[BrandRow]] = ctx.run(brandSchema)
}
object BrandTableService{
  val live = ZLayer.succeed(BrandTableService())
}
