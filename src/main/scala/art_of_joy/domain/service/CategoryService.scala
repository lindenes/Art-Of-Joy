package art_of_joy.domain.service

import art_of_joy.application.model.Request.*
import art_of_joy.domain.model._
import art_of_joy.domain.model.Errors.DomainError
import art_of_joy.repository.model.{CategoryRow, SubCategoryRow}
import art_of_joy.repository.service.category.CategoryTable
import zio.*

import javax.sql.DataSource

object CategoryService {
  def addCategories(categories:List[CategoryAdd]): ZIO[DataSource & CategoryTable, DomainError, List[(CategoryRow, List[SubCategoryRow])]] =
    ZIO.collectAll(
      categories.map(category =>
        for{
          newCategory <- CategoryTable.addCategory(category.name)
          newSubCategories <- ZIO.collectAll(category.subNames.map(name =>
            CategoryTable.addSubCategory(name, newCategory.id)
          ))
        }yield newCategory -> newSubCategories
      )
    )
  
  def getCategories: ZIO[DataSource & CategoryTable, DomainError, List[Category]] =
    CategoryTable.getCategoryWithSub.map(
      fullList =>
        fullList.map(_._1).distinct.map(category =>
          Category(
            category.id, category.name,
            fullList.map(_._2).filter { sc =>
              sc match
                case Some(value) => value.categoryId == category.id
                case None => false
            }.collect { case Some(value) => SubCategory(value.id, value.name, value.categoryId) }
          )
        )

    )
    
}
