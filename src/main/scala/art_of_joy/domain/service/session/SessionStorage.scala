package art_of_joy.domain.service.session
import art_of_joy.domain.model.StoragePerson
import art_of_joy.domain.service.person.Person
import art_of_joy.repository.model.PersonRow
import zio.*
trait SessionStorage {
  def get(key: String): ZIO[Any, Nothing, Option[StoragePerson]]

  def put(key: String, data: StoragePerson): UIO[Unit]

  def updateTime(key: String): ZIO[Scope, Throwable, Unit]

  def setAcceptCode(key: String, code: String): ZIO[Scope, Throwable, Unit]

  def updatePerson(key: String, person: PersonRow): ZIO[Scope, Throwable, Unit]

  def clearAcceptCode(key: String): ZIO[Scope, Throwable, Unit]

  def clearPerson(key: String): UIO[Unit]

  def clearPersons(key: List[String]): UIO[Unit]

  def checkInactivePersons: ZIO[Scope, Throwable, List[String]]

  def checkStorage: UIO[Map[String, StoragePerson]]
}
object SessionStorage{
  def get(key: String) = ZIO.serviceWithZIO[SessionStorage](_.get(key))

  def put(key: String, data: StoragePerson) = ZIO.serviceWithZIO[SessionStorage](_.put(key, data))

  def updateTime(key: String) = ZIO.serviceWithZIO[SessionStorage](_.updateTime(key))

  def setAcceptCode(key: String, code: String) = ZIO.serviceWithZIO[SessionStorage](_.setAcceptCode(key, code))

  def updatePerson(key: String, person: PersonRow) = ZIO.serviceWithZIO[SessionStorage](_.updatePerson(key, person))

  def clearAcceptCode(key: String) = ZIO.serviceWithZIO[SessionStorage](_.clearAcceptCode(key))

  def clearPerson(key: String) = ZIO.serviceWithZIO[SessionStorage](_.clearPerson(key))

  def clearPersons(key: List[String]) = ZIO.serviceWithZIO[SessionStorage](_.clearPersons(key))

  def checkInactivePersons = ZIO.serviceWithZIO[SessionStorage](_.checkInactivePersons)

  def checkStorage = ZIO.serviceWithZIO[SessionStorage](_.checkStorage)

}