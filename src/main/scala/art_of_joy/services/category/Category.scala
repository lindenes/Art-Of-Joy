package art_of_joy.services.category
import art_of_joy.model.category.{CategoryAdd, FullCategory}
import art_of_joy.repository.category.CategoryTable
import art_of_joy.repository.subcategory.SubCategoryTable
import zio.*

import javax.sql.DataSource
trait Category {
  def getFullCategoryList:ZIO[DataSource & CategoryTable, Throwable, List[FullCategory]]
  def addCategories(categories:List[CategoryAdd]):ZIO[DataSource & CategoryTable & SubCategoryTable, Throwable, String]
}
object Category{
  def getFullCategoryList: ZIO[DataSource & CategoryTable & Category, Throwable, List[FullCategory]] = 
    ZIO.serviceWithZIO[Category](_.getFullCategoryList)
  def addCategories(categories:List[CategoryAdd]): ZIO[DataSource & CategoryTable & SubCategoryTable & Category, Throwable, String] =
    ZIO.serviceWithZIO[Category](_.addCategories(categories))
}