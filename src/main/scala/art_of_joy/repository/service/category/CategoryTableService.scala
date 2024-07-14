package art_of_joy.repository.service.category

import art_of_joy.ctx
import art_of_joy.model.category.BrandRow
import io.getquill.*
import zio.{ZIO, ZLayer}
import art_of_joy.repository.*
import art_of_joy.repository.model.{CategoryRow, SubCategoryRow}

import javax.sql.DataSource
class CategoryTableService extends CategoryTable{
  import ctx._
  override def getCategories: ZIO[DataSource, Throwable, List[CategoryRow]] = ctx.run(categorySchema)

  override def getCategoryWithSub: ZIO[DataSource, Throwable, List[(CategoryRow, Option[SubCategoryRow])]] =
    ctx.run(
      categorySchema.leftJoin(subCategorySchema).on({case (c, sc) => sc.categoryId == c.id})
    )

  override def addCategory(name: String): ZIO[DataSource, Throwable, CategoryRow] =
    ctx.run(
        categorySchema.insert(_.name -> lift(name)).returning(v => v)
    )

  override def addSubCategory(name: String, categoryId: Long): ZIO[DataSource, Throwable, SubCategoryRow] =
    ctx.run(
      subCategorySchema.insert(_.name -> lift(name), _.categoryId -> lift(categoryId)).returning(v => v)
    )

  override def getBrands: ZIO[DataSource, Throwable, List[BrandRow]] =
    ctx.run(brandSchema)
}
object CategoryTableService{
  val live = ZLayer.succeed(CategoryTableService())
}