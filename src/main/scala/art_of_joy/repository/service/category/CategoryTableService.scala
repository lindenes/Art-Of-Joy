package art_of_joy.repository.service.category

import art_of_joy.ctx
import art_of_joy.domain.model.Errors.{DataBaseError, DomainError}
import art_of_joy.model.category.BrandRow
import io.getquill.*
import zio.{ZIO, ZLayer}
import art_of_joy.repository.*
import art_of_joy.repository.model.{CategoryRow, SubCategoryRow}

import javax.sql.DataSource
class CategoryTableService extends CategoryTable{
  import ctx._
  override def getCategories: ZIO[DataSource, DomainError, List[CategoryRow]] = 
    ctx.run(categorySchema)
      .mapError(ex => DataBaseError(exception = ex))

  override def getCategoryWithSub: ZIO[DataSource, DomainError, List[(CategoryRow, Option[SubCategoryRow])]] =
    ctx.run(
      categorySchema.leftJoin(subCategorySchema).on({case (c, sc) => sc.categoryId == c.id})
    ).mapError(ex => DataBaseError(exception = ex))

  override def addCategory(name: String): ZIO[DataSource, DomainError, CategoryRow] =
    ctx.run(
        categorySchema.insert(_.name -> lift(name)).returning(v => v)
    ).mapError(ex => DataBaseError(exception = ex))

  override def addSubCategory(name: String, categoryId: Long): ZIO[DataSource, DomainError, SubCategoryRow] =
    ctx.run(
      subCategorySchema.insert(_.name -> lift(name), _.categoryId -> lift(categoryId)).returning(v => v)
    ).mapError(ex => DataBaseError(exception = ex))

  override def getBrands: ZIO[DataSource, DomainError, List[BrandRow]] =
    ctx.run(brandSchema)
      .mapError(ex => DataBaseError(exception = ex))
}
object CategoryTableService{
  val live = ZLayer.succeed(CategoryTableService())
}