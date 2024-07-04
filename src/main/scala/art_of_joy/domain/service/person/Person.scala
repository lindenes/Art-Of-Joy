package art_of_joy.domain.service.person

import art_of_joy.domain.service.email.EmailService
import art_of_joy.domain.service.session.SessionStorage
import art_of_joy.model.http.{HttpResponse, HttpValidationFields}
import art_of_joy.model.person.{RegPerson, SetPassword}
import art_of_joy.repository.model.PersonRow
import zio.*

import javax.sql.DataSource
trait Person {
  def getAllPersons(startRow:Int, endRow:Option[Int]):ZIO[DataSource, Throwable, List[PersonRow]]
  def getPersonByEmail(email:String):ZIO[DataSource,Throwable, List[PersonRow]]
  def getPersonByID(personID:Long):ZIO[DataSource,Throwable, PersonRow]
  def authPerson(email:String, password:String):ZIO[DataSource, Throwable, List[PersonRow]]
  def authPersonOnEmail(email:String):ZIO[DataSource & SessionStorage, Throwable, String]
  def checkEmail(email:String):ZIO[DataSource, Throwable, Boolean]
  def checkPhone(phone: String): ZIO[DataSource, Throwable, Boolean]
  def addPerson(person: PersonRow):ZIO[DataSource, Throwable, PersonRow]
  def emailRegistration(email:String):ZIO[SessionStorage & DataSource & EmailService,Throwable,Either[String, List[HttpValidationFields]]]
  def phoneRegistration(phone:String):ZIO[SessionStorage & DataSource,Throwable,Either[String, List[HttpValidationFields]]]
  def setPassword(personID:Int, newPassword:String, repeatPassword:String):ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]]
  def updatePassword(personID:Int, newPassword:String, repeatPassword:String, oldPassword:String):ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]]
  def setPersonInfo(id:Long, surName:String, firstName:String, middleName:Option[String]):ZIO[DataSource, Throwable, Long]
}
object Person{
  def getAllPersons(startRow: Int, endRow: Option[Int]) =
    ZIO.serviceWithZIO[Person](_.getAllPersons(startRow, endRow))

  def getPersonByEmail(email: String) =
    ZIO.serviceWithZIO[Person](_.getPersonByEmail(email))

  def getPersonByID(personID: Long) =
    ZIO.serviceWithZIO[Person](_.getPersonByID(personID))

  def authPerson(email: String, password: String) =
    ZIO.serviceWithZIO[Person](_.authPerson(email, password))

  def authPersonOnEmail(email: String) =
    ZIO.serviceWithZIO[Person](_.authPersonOnEmail(email))

  def checkEmail(email: String) =
    ZIO.serviceWithZIO[Person](_.checkEmail(email))

  def checkPhone(phone: String) =
    ZIO.serviceWithZIO[Person](_.checkPhone(phone))

  def addPerson(person: PersonRow) =
    ZIO.serviceWithZIO[Person](_.addPerson(person))

  def emailRegistration(email: String)=
    ZIO.serviceWithZIO[Person](_.emailRegistration(email))

  def phoneRegistration(phone: String)=
    ZIO.serviceWithZIO[Person](_.phoneRegistration(phone))

  def setPassword(personID: Int, newPassword: String, repeatPassword: String)=
    ZIO.serviceWithZIO[Person](_.setPassword(personID, newPassword, repeatPassword))

  def updatePassword(personID: Int, newPassword: String, repeatPassword: String, oldPassword: String)=
    ZIO.serviceWithZIO[Person](_.updatePassword(personID, newPassword, repeatPassword, oldPassword))

  def setPersonInfo(id: Long, surName: String, firstName: String, middleName: Option[String])=
    ZIO.serviceWithZIO[Person](_.setPersonInfo(id, surName, firstName, middleName))
}