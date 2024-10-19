package art_of_joy.repository.service.category

import art_of_joy.domain.model.Errors.DomainError
import art_of_joy.repository.model._

import javax.sql.DataSource
import zio.*

trait CategoryTable {
  def getCategories:ZIO[DataSource,DomainError, List[CategoryRow]]
  def getCategoryWithSub:ZIO[DataSource, DomainError,  List[(CategoryRow, Option[SubCategoryRow])]]
  def addCategory(name:String):ZIO[DataSource,DomainError, CategoryRow]
  def addSubCategory(name:String, categoryId:Long):ZIO[DataSource,DomainError,SubCategoryRow]
  def getBrands:ZIO[DataSource,DomainError, List[BrandRow]]
  def addBrand(name:String):ZIO[DataSource,DomainError,BrandRow]
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
  
  def addBrand(name:String) =
    ZIO.serviceWithZIO[CategoryTable](_.addBrand(name))
}