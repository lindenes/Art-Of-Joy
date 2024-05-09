package art_of_joy.services
import art_of_joy.ctx
import art_of_joy.services.interfaces.CategoryTrait
import zio.{ZIO, ZLayer}
import io.getquill.*
import art_of_joy.model.category._

import javax.sql.DataSource
object CategoryLayer {
  import ctx._
  val live = ZLayer.succeed(
    new CategoryTrait {
      override def getFullCategoryList: ZIO[DataSource, Throwable, List[ClientCategory]] =
        for{
          categoryList <- ctx.run(
            quote {
              query[Category].leftJoin(query[Subcategory]).on({case (c, sc) => sc.category_id == c.id})
            }
          )
          fullCategory <- ZIO.from(
            categoryList.map(_._1).distinct.map(category =>
              ClientCategory(
                category.id,
                category.name,
                categoryList.map(_._2).filter{sc =>
                  sc match
                    case Some(value) => value.category_id == category.id
                    case None => false
                }.collect{case Some(value) => value}
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
              liftQuery(subCategories).foreach(sc => query[Subcategory].insert(_.name -> sc.name, _.category_id -> sc.categoryID))
            }
          )
        }yield data

      override def getBrandList: ZIO[DataSource, Throwable, List[Brand]] =
        ctx.run(
          quote{query[Brand]}
        )
    }
  )
}
