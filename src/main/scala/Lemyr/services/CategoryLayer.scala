package lemyr.services
import lemyr.ctx
import lemyr.services.interfaces.CategoryTrait
import zio.{ZIO, ZLayer}
import io.getquill.*
import lemyr.model.category.{Category, CategoryFull, SubCategoryFromClient, Sub_category}

import javax.sql.DataSource
object CategoryLayer {
  import ctx._
  val live = ZLayer.succeed(
    new CategoryTrait {
      override def getFullCategoryList: ZIO[DataSource, Throwable, List[CategoryFull]] =
        for{
          categoryList <- ctx.run(
            quote {
              query[Category].leftJoin(query[Sub_category]).on({case (c, sc) => sc.category_id == c.id})
            }
          )
          fullCategory <- ZIO.from(
            categoryList.map(_._1).distinct.map(category =>
              CategoryFull(
                category,
                categoryList.map(_._2).filter{sc =>
                  sc match
                    case Some(value) => value.category_id == category.id
                    case None => false
                }
            ))
          )
        }yield fullCategory

      override def addCategory(names: List[String]): ZIO[DataSource, Throwable, Unit] =
        for{
          data <- ctx.run(
            quote{
              liftQuery(names).foreach(name => query[Category].insert(_.name -> name))
            }
          )
        }yield data

      override def addSubCategory(subCategories: List[SubCategoryFromClient]): ZIO[DataSource, Throwable, Unit] =
        for{
          data <- ctx.run(
            quote{
              liftQuery(subCategories).foreach(sc => query[Sub_category].insert(_.name -> sc.name, _.category_id -> sc.categoryID))
            }
          )
        }yield data
    }
  )
}
