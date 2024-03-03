package art_of_joy.services.interfaces

import art_of_joy.model.person.{Person, RegPerson}
import zio.*

import javax.sql.DataSource
trait UserTrait {
  def getAllPersons(startRow:Int, endRow:Option[Int]):ZIO[DataSource, Throwable, List[Person]]
  def authUser(email:String, password:String):ZIO[DataSource, Throwable, List[Person]]
  def checkEmail(email:String):ZIO[DataSource, Throwable, Boolean]
  def checkNumber(number: String): ZIO[DataSource, Throwable, Boolean]
  def passwordRegistration(email:String, password:String, number:String):ZIO[DataSource,Throwable,Either[Person, List[String]]]
  def emailRegistration(email:String, number:String):ZIO[DataSource,Throwable,Either[Person, List[String]]]
}
