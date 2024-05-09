package art_of_joy.services.interfaces

import art_of_joy.model.http.{HttpResponse, HttpValidationFields}
import art_of_joy.model.person.{Person, RegPerson, SetPassword}
import zio.*

import javax.sql.DataSource
trait PersonTrait {
  def getAllPersons(startRow:Int, endRow:Option[Int]):ZIO[DataSource, Throwable, List[Person]]
  def getPersonByEmail(email:String):ZIO[DataSource,Throwable, List[Person]]
  def getPersonByID(personID:Long):ZIO[DataSource,Throwable, Person]
  def authPerson(email:String, password:String):ZIO[DataSource, Throwable, List[Person]]
  def authPersonOnEmail(email:String):ZIO[DataSource & SessionStorageTrait, Throwable, String]
  def checkEmail(email:String):ZIO[DataSource, Throwable, Boolean]
  def checkPhone(phone: String): ZIO[DataSource, Throwable, Boolean]
  def addPerson(person: Person):ZIO[DataSource, Throwable, Person]
  def emailRegistration(email:String):ZIO[SessionStorageTrait & DataSource & EmailServiceTrait,Throwable,Either[String, List[HttpValidationFields]]]
  def phoneRegistration(phone:String):ZIO[SessionStorageTrait & DataSource,Throwable,Either[String, List[HttpValidationFields]]]
  def setPassword(personID:Int, newPassword:String, repeatPassword:String):ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]]
  def updatePassword(personID:Int, newPassword:String, repeatPassword:String, oldPassword:String):ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]]
  def setPersonInfo(id:Long, surName:String, firstName:String, middleName:Option[String]):ZIO[DataSource, Throwable, Long]
}
