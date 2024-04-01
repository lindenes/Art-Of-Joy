package art_of_joy.services.interfaces

import art_of_joy.model.person.Person
import art_of_joy.services.SessionStorageLayer.StoragePerson
import zio.*

trait SessionStorageTrait(ref: Ref[Map[String, StoragePerson]]) {
  def get(key: String): ZIO[Any, Nothing, Option[StoragePerson]]
  def put(key: String, data: StoragePerson): UIO[Unit]
  def updateTime(key: String): ZIO[Scope, Throwable, Unit]
  def setAcceptCode(key: String, code:Option[String]): ZIO[Scope, Throwable, Unit]
  def updatePerson(key:String, person:Person): ZIO[Scope, Throwable, Unit]
  def clearAcceptCode(key: String): ZIO[Scope, Throwable, Unit]
  def clearPerson(key: String): UIO[Unit]
  def clearPersons(key: List[String]): UIO[Unit]
  def checkInactivePersons: ZIO[Scope, Throwable, List[String]]
  def checkStorage(): UIO[Map[String, StoragePerson]]
}

