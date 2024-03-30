package art_of_joy.services.interfaces

import art_of_joy.model.person.Person
import art_of_joy.services.SessionStorageLayer.StorageUser
import zio.*

trait SessionStorageTrait(ref: Ref[Map[String, StorageUser]]) {
  def get(key: String): ZIO[Any, Nothing, Option[StorageUser]]
  def put(key: String, data: StorageUser): UIO[Unit]
  def updateTime(key: String): ZIO[Scope, Throwable, Unit]
  def setAcceptCode(key: String, code:String): ZIO[Scope, Throwable, Unit]
  def updatePerson(key:String, person:Person): ZIO[Scope, Throwable, Unit]
  def clearAcceptCode(key: String): ZIO[Scope, Throwable, Unit]
  def clearUser(key: String): UIO[Unit]
  def clearUsers(key: List[String]): UIO[Unit]
  def checkInactiveUsers: ZIO[Scope, Throwable, List[String]]
  def checkStorage(): UIO[Map[String, StorageUser]]
}

