package art_of_joy.repository.service.category

import art_of_joy.repository.model.{CategoryRow, SubCategoryRow}

import javax.sql.DataSource
import zio.*
trait CategoryTable {
  def getCategory:ZIO[DataSource,Throwable, List[CategoryRow]]
  def getCategoryWithSub:ZIO[DataSource, Throwable,  List[(CategoryRow, Option[SubCategoryRow])]]
  def addCategory(name:String):ZIO[DataSource,Throwable, CategoryRow]
}
object CategoryTable{
  def getCategory: ZIO[DataSource & CategoryTable, Throwable, List[CategoryRow]] = 
    ZIO.serviceWithZIO[CategoryTable](_.getCategory)

  def getCategoryWithSub: ZIO[DataSource & CategoryTable, Throwable, List[(CategoryRow, Option[SubCategoryRow])]] =
    ZIO.serviceWithZIO[CategoryTable](_.getCategoryWithSub)
    
  def addCategory(name:String): ZIO[DataSource & CategoryTable, Throwable, CategoryRow] =
    ZIO.serviceWithZIO[CategoryTable](_.addCategory(name))
}