package art_of_joy.services.interfaces

import art_of_joy.model.http.{HttpResponse, HttpValidationFields}
import art_of_joy.model.person.{Person, RegPerson, SetPassword}
import zio.*

import javax.sql.DataSource
trait PersonService {
  def getAllPersons(startRow:Int, endRow:Option[Int]):ZIO[DataSource, Throwable, List[Person]]
  def getPersonByEmail(email:String):ZIO[DataSource,Throwable, List[Person]]
  def getPersonByID(personID:Long):ZIO[DataSource,Throwable, Person]
  def authPerson(email:String, password:String):ZIO[DataSource, Throwable, List[Person]]
  def authPersonOnEmail(email:String):ZIO[DataSource & SessionStorageService, Throwable, String]
  def checkEmail(email:String):ZIO[DataSource, Throwable, Boolean]
  def checkPhone(phone: String): ZIO[DataSource, Throwable, Boolean]
  def addPerson(person: Person):ZIO[DataSource, Throwable, Person]
  def emailRegistration(email:String):ZIO[SessionStorageService & DataSource & EmailService,Throwable,Either[String, List[HttpValidationFields]]]
  def phoneRegistration(phone:String):ZIO[SessionStorageService & DataSource,Throwable,Either[String, List[HttpValidationFields]]]
  def setPassword(personID:Int, newPassword:String, repeatPassword:String):ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]]
  def updatePassword(personID:Int, newPassword:String, repeatPassword:String, oldPassword:String):ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]]
  def setPersonInfo(id:Long, surName:String, firstName:String, middleName:Option[String]):ZIO[DataSource, Throwable, Long]
}
object PersonService{
  def getAllPersons(startRow: Int, endRow: Option[Int]) =
    ZIO.serviceWithZIO[PersonService](_.getAllPersons(startRow, endRow))

  def getPersonByEmail(email: String) =
    ZIO.serviceWithZIO[PersonService](_.getPersonByEmail(email))

  def getPersonByID(personID: Long) =
    ZIO.serviceWithZIO[PersonService](_.getPersonByID(personID))

  def authPerson(email: String, password: String) =
    ZIO.serviceWithZIO[PersonService](_.authPerson(email, password))

  def authPersonOnEmail(email: String) =
    ZIO.serviceWithZIO[PersonService](_.authPersonOnEmail(email))

  def checkEmail(email: String) =
    ZIO.serviceWithZIO[PersonService](_.checkEmail(email))

  def checkPhone(phone: String) =
    ZIO.serviceWithZIO[PersonService](_.checkPhone(phone))

  def addPerson(person: Person) =
    ZIO.serviceWithZIO[PersonService](_.addPerson(person))

  def emailRegistration(email: String)=
    ZIO.serviceWithZIO[PersonService](_.emailRegistration(email))

  def phoneRegistration(phone: String)=
    ZIO.serviceWithZIO[PersonService](_.phoneRegistration(phone))

  def setPassword(personID: Int, newPassword: String, repeatPassword: String)=
    ZIO.serviceWithZIO[PersonService](_.setPassword(personID, newPassword, repeatPassword))

  def updatePassword(personID: Int, newPassword: String, repeatPassword: String, oldPassword: String)=
    ZIO.serviceWithZIO[PersonService](_.updatePassword(personID, newPassword, repeatPassword, oldPassword))

  def setPersonInfo(id: Long, surName: String, firstName: String, middleName: Option[String])=
    ZIO.serviceWithZIO[PersonService](_.setPersonInfo(id, surName, firstName, middleName))
}