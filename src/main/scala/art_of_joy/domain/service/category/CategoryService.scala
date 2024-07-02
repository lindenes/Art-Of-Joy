package art_of_joy.domain.service.category

import art_of_joy.model.category.{CategoryAdd, FullCategory, SubCategory}
import art_of_joy.repository.service.category.CategoryTable
import art_of_joy.repository.service.subcategory.SubCategoryTable
import zio.{ZIO, ZLayer}

import javax.sql.DataSource

class CategoryService extends Category {
   override def getFullCategoryList: ZIO[DataSource & CategoryTable, Throwable, List[FullCategory]] =
    CategoryTable.getCategoryWithSub.map(
      fullList =>
        fullList.map(_._1).distinct.map(category =>
          FullCategory(
            category.id, category.name,
            fullList.map(_._2).filter{sc =>
              sc match
                case Some(value) => value.categoryID == category.id
                case None => false
            }.collect{case Some(value) => SubCategory(value.id, value.name, value.categoryID)}
          )
        )

    )

  override def addCategories(categories: List[CategoryAdd]): ZIO[DataSource & SubCategoryTable & DataSource & CategoryTable, Throwable, String] =
      ZIO.collectAll(
        categories.map(category =>
          for{
            newCategory <- CategoryTable.addCategory(category.name)
            newSubCategories <- ZIO.collectAll(category.subNames.map(name =>
              SubCategoryTable.addSubCategory(name, newCategory.id)
            ))
          }yield newCategory -> newSubCategories
        )
      ).map(addedData => 
        s"Добавлено ${addedData.length} категорий и ${addedData.flatMap(_._2).length} подкатегории"
      )
}
object CategoryService{
  val live = ZLayer.succeed(CategoryService())
}