package art_of_joy.domain.service.category

import art_of_joy.model.category.{CategoryAdd, FullCategory}
import art_of_joy.repository.service.category.CategoryTable
import art_of_joy.repository.service.subcategory.SubCategoryTable
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