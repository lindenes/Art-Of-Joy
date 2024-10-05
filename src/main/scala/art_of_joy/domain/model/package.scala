package art_of_joy.domain

import art_of_joy.Role

package object model {
  
  case class SubCategory(id: Long, name: String, categoryId: Long)
  case class Category(id: Long, name: String, subCategories: List[SubCategory])

  case class Person(
                     surname: Option[String],
                     email: String,
                     phone: Option[String],
                     role: Role,
                     firstname: Option[String],
                     middleName: Option[String],
                     id: Long,
                     passwordHash: Option[String],
                     isConfirmEmail: Boolean,
                     isConfirmPhone: Boolean
                   )

  case class StoragePerson(person: Person, lastVisitTime: Long, acceptCode: String = "")
  
  case class CartProduct(
                          id:Long,
                          productName:String,
                          productId:Long,
                          count:Int,
                          price:Double
                        )
}
