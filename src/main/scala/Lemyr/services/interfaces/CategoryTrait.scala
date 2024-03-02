package lemyr.services.interfaces

import io.getquill.*
import lemyr.model.category.{CategoryFull, SubCategoryFromClient}
import zio.*

import javax.sql.DataSource
trait CategoryTrait {
  def getFullCategoryList:ZIO[DataSource,Throwable, List[CategoryFull] ]

  def addCategory(name:List[String]):ZIO[DataSource, Throwable, Unit]
  def addSubCategory(subCategories:List[SubCategoryFromClient]):ZIO[DataSource, Throwable, Unit]
}
