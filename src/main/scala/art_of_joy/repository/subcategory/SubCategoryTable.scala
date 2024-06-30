package art_of_joy.repository.subcategory

import art_of_joy.model.category.SubCategoryRow

import javax.sql.DataSource
import zio.*
trait SubCategoryTable {
  def getSubCategory:ZIO[DataSource,Throwable, List[SubCategoryRow]]
  def addSubCategory(name:String, categoryID:Long):ZIO[DataSource,Throwable, SubCategoryRow]
}
object SubCategoryTable{
  def getSubCategory: ZIO[DataSource & SubCategoryTable, Throwable, List[SubCategoryRow]] = 
    ZIO.serviceWithZIO[SubCategoryTable](_.getSubCategory)
  def addSubCategory(name:String, categoryID:Long): ZIO[DataSource & SubCategoryTable, Throwable, SubCategoryRow] =
    ZIO.serviceWithZIO[SubCategoryTable](_.addSubCategory(name, categoryID))
}
