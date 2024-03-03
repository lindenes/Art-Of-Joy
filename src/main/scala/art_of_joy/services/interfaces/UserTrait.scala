package art_of_joy.services.interfaces

import art_of_joy.model.person.Person
import zio.*

import javax.sql.DataSource
trait UserTrait {
  def getAllPersons(startRow:Int, endRow:Option[Int]):ZIO[DataSource, Throwable, List[Person]]

  def authUser(email:String, password:String):ZIO[DataSource, Throwable, List[Person]]
}
