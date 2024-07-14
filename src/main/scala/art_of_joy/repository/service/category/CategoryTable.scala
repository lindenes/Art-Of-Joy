package art_of_joy.repository.service.category

import art_of_joy.model.category.BrandRow
import art_of_joy.repository.model.{CategoryRow, SubCategoryRow}

import javax.sql.DataSource
import zio.*
trait CategoryTable {
  def getCategories:ZIO[DataSource,Throwable, List[CategoryRow]]
  def getCategoryWithSub:ZIO[DataSource, Throwable,  List[(CategoryRow, Option[SubCategoryRow])]]
  def addCategory(name:String):ZIO[DataSource,Throwable, CategoryRow]
  def addSubCategory(name:String, categoryId:Long):ZIO[DataSource,Throwable,SubCategoryRow]
  def getBrands:ZIO[DataSource,Throwable, List[BrandRow]]
}
object CategoryTable{
  def getAllCategories = 
    ZIO.serviceWithZIO[CategoryTable](_.getCategories)

  def getCategoryWithSub =
    ZIO.serviceWithZIO[CategoryTable](_.getCategoryWithSub)
    
  def addCategory(name:String) =
    ZIO.serviceWithZIO[CategoryTable](_.addCategory(name))

  def addSubCategory(name:String, categoryId:Long) =
    ZIO.serviceWithZIO[CategoryTable](_.addSubCategory(name, categoryId))
    
  def getBrands =
    ZIO.serviceWithZIO[CategoryTable](_.getBrands)
}