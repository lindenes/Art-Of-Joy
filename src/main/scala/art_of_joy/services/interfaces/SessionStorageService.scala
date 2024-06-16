package art_of_joy.services.interfaces

import art_of_joy.model.person.Person
import art_of_joy.services.SessionStorageLayer.StoragePerson
import zio.*

trait SessionStorageService(ref: Ref[Map[String, StoragePerson]]) {
  def get(key: String): ZIO[Any, Nothing, Option[StoragePerson]]
  def put(key: String, data: StoragePerson): UIO[Unit]
  def updateTime(key: String): ZIO[Scope, Throwable, Unit]
  def setAcceptCode(key: String, code:String): ZIO[Scope, Throwable, Unit]
  def updatePerson(key:String, person:Person): ZIO[Scope, Throwable, Unit]
  def clearAcceptCode(key: String): ZIO[Scope, Throwable, Unit]
  def clearPerson(key: String): UIO[Unit]
  def clearPersons(key: List[String]): UIO[Unit]
  def checkInactivePersons: ZIO[Scope, Throwable, List[String]]
  def checkStorage: UIO[Map[String, StoragePerson]]
}
object SessionStorageService{
  def get(key: String) =
    ZIO.serviceWithZIO[SessionStorageService](_.get(key))

  def put(key: String, data: StoragePerson) =
    ZIO.serviceWithZIO[SessionStorageService](_.put(key, data))
    
  def updateTime(key: String) =
    ZIO.serviceWithZIO[SessionStorageService](_.updateTime(key))
  
  def setAcceptCode(key: String, code: String) =
    ZIO.serviceWithZIO[SessionStorageService](_.setAcceptCode(key, code))
  
  def updatePerson(key: String, person: Person) =
    ZIO.serviceWithZIO[SessionStorageService](_.updatePerson(key, person))
  
  def clearAcceptCode(key: String) =
    ZIO.serviceWithZIO[SessionStorageService](_.clearAcceptCode(key))
  
  def clearPerson(key: String) =
    ZIO.serviceWithZIO[SessionStorageService](_.clearPerson(key))
  
  def clearPersons(key: List[String]) =
    ZIO.serviceWithZIO[SessionStorageService](_.clearPersons(key))
  
  def checkInactivePersons =
    ZIO.serviceWithZIO[SessionStorageService](_.checkInactivePersons)
  
  def checkStorage =
    ZIO.serviceWithZIO[SessionStorageService](_.checkStorage)

}

