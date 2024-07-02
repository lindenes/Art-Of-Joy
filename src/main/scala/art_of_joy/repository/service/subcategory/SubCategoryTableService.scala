package art_of_joy.repository.service.subcategory

import art_of_joy.ctx
import zio.{ZIO, ZLayer}
import io.getquill.*

import javax.sql.DataSource
import art_of_joy.repository.*
import art_of_joy.repository.model.SubCategoryRow

import java.sql.SQLException
class SubCategoryTableService extends SubCategoryTable {
  import ctx._
  override def getSubCategory: ZIO[DataSource, Throwable, List[SubCategoryRow]] = ctx.run(subCategorySchema)

  override def addSubCategory(name:String,categoryID:Long): ZIO[DataSource, SQLException, SubCategoryRow] =
    ctx.run(
      subCategorySchema.insert(_.name -> lift(name), _.categoryID -> lift(categoryID)).returning(value => value)
    )
}
object SubCategoryTableService{
  val live = ZLayer.succeed(SubCategoryTableService())
}