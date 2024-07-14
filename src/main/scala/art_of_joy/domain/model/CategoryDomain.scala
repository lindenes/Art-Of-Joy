package art_of_joy.domain.model

object CategoryDomain{
  case class SubCategory(id:Long, name:String, categoryId:Long)
  case class Category(id:Long, name:String, subCategories:List[SubCategory])
}
