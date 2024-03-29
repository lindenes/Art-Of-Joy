package art_of_joy.services.interfaces

import io.getquill.*
import art_of_joy.model.category.{CategoryFull, SubCategoryFromClient}
import zio.*

import javax.sql.DataSource
trait CategoryTrait {
  def getFullCategoryList:ZIO[DataSource,Throwable, List[CategoryFull] ]

  def addCategory(name:List[String]):ZIO[DataSource, Throwable, Unit]
  def addSubCategory(subCategories:List[SubCategoryFromClient]):ZIO[DataSource, Throwable, Unit]
}
