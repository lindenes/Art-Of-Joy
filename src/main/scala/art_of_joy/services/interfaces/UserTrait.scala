package art_of_joy.services.interfaces

import art_of_joy.model.person.{Person, RegPerson}
import zio.*

import javax.sql.DataSource
trait UserTrait {
  def getAllPersons(startRow:Int, endRow:Option[Int]):ZIO[DataSource, Throwable, List[Person]]
  def getPersonByEmail(email:String):ZIO[DataSource,Throwable, List[Person]]
  def authUser(email:String, password:String):ZIO[DataSource, Throwable, List[Person]]
  def authUserOnEmail(email:String):ZIO[DataSource & SessionStorageTrait, Throwable, String]
  def checkEmail(email:String):ZIO[DataSource, Throwable, Boolean]
  def checkPhone(phone: String): ZIO[DataSource, Throwable, Boolean]
  def addPerson(person: Person):ZIO[DataSource, Throwable, Person]
  def emailRegistration(email:String):ZIO[SessionStorageTrait & DataSource & EmailServiceTrait,Throwable,Either[String, List[String]]]
  def phoneRegistration(phone:String):ZIO[SessionStorageTrait & DataSource,Throwable,Either[String, List[String]]]
}
