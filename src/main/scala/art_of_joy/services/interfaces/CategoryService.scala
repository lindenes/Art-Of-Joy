package art_of_joy.services.interfaces

import io.getquill.*
import art_of_joy.model.category._
import zio.*

import javax.sql.DataSource
trait CategoryService {
  def getFullCategoryList:ZIO[DataSource,Throwable, List[ClientCategory] ]
  def addCategory(name:List[String]):ZIO[DataSource, Throwable, Unit]
  def addSubCategory(subCategories:List[SubCategoryFromClient]):ZIO[DataSource, Throwable, Unit]
  def getBrandList:ZIO[DataSource, Throwable, List[Brand]]
}
object CategoryService{
  def getFullCategoryList =
    ZIO.serviceWithZIO[CategoryService](_.getFullCategoryList)

  def addCategory(name: List[String]) =
    ZIO.serviceWithZIO[CategoryService](_.addCategory(name))

  def addSubCategory(subCategories: List[SubCategoryFromClient]) =
    ZIO.serviceWithZIO[CategoryService](_.addSubCategory(subCategories))

  def getBrandList =
    ZIO.serviceWithZIO[CategoryService](_.getBrandList)
}