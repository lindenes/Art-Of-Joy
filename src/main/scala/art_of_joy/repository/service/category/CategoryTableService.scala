package art_of_joy.repository.service.category

import art_of_joy.ctx
import io.getquill.*
import zio.{ZIO, ZLayer}
import art_of_joy.repository.*
import art_of_joy.repository.model.{CategoryRow, SubCategoryRow}
import art_of_joy.repository.service.subcategory.SubCategoryTableService

import javax.sql.DataSource
class CategoryTableService extends CategoryTable{
  import ctx._
  override def getCategory: ZIO[DataSource, Throwable, List[CategoryRow]] = ctx.run(categorySchema)

  override def getCategoryWithSub: ZIO[DataSource, Throwable, List[(CategoryRow, Option[SubCategoryRow])]] =
    ctx.run(
      categorySchema.leftJoin(subCategorySchema).on({case (c, sc) => sc.categoryID == c.id})
    )

  override def addCategory(name: String): ZIO[DataSource, Throwable, CategoryRow] =
    ctx.run(
        categorySchema.insert(_.name -> lift(name)).returning(value => value)
    )
}
object CategoryTableService{
  val live = ZLayer.succeed(CategoryTableService())
}