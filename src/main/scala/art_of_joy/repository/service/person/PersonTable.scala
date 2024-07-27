package art_of_joy.repository.service.person

import art_of_joy.domain.model.Errors.DomainError
import art_of_joy.domain.model.Person
import art_of_joy.repository.model.PersonRow
import zio.*

import javax.sql.DataSource

trait PersonTable {
  def getPersonByEmail(email: String): ZIO[DataSource, DomainError, List[PersonRow]]
  def getPersonByPhone(number:String): ZIO[DataSource, DomainError, List[PersonRow]]
  def getPersonById(personId: Long): ZIO[DataSource, DomainError, List[PersonRow]]
  def addPerson(person: Person): ZIO[DataSource, DomainError, PersonRow]
  def getAllPersons(startRow: Int, endRow: Option[Int]): ZIO[DataSource, DomainError, List[PersonRow]]
  def setPersonData(id:Long, passwordHash:Option[String], surname:Option[String], firstname:Option[String], middleName:Option[String]): ZIO[DataSource, DomainError, Long]
}
object PersonTable{
  def getPersonByEmail(email: String) =
    ZIO.serviceWithZIO[PersonTable](_.getPersonByEmail(email))
  def getPersonByPhone(phone:String) =
    ZIO.serviceWithZIO[PersonTable](_.getPersonByPhone(phone))
  def addPerson(person:Person) =
    ZIO.serviceWithZIO[PersonTable](_.addPerson(person))
  def getPersonById(personId:Long)=
    ZIO.serviceWithZIO[PersonTable](_.getPersonById(personId))
  def getAllPersons(startRow:Int, endRow:Option[Int]) =
    ZIO.serviceWithZIO[PersonTable](_.getAllPersons(startRow, endRow))
  def setPersonData(id: Long, passwordHash: Option[String] = None, surname: Option[String] = None, firstname: Option[String] = None, middleName: Option[String] = None) =
    ZIO.serviceWithZIO[PersonTable](_.setPersonData(id, passwordHash, surname, firstname, middleName))
}
