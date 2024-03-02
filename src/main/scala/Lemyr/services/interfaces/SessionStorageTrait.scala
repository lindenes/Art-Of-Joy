package lemyr.services.interfaces

import lemyr.services.SessionStorageLayer.StorageUser
import zio.*

trait SessionStorageTrait(ref: Ref[Map[String, StorageUser]]) {
  def get(key: String): ZIO[Any, Nothing, Option[StorageUser]]

  def put(key: String, data: StorageUser): UIO[Unit]

  def updateTime(key: String): ZIO[Scope, Throwable, Unit]

  def clearUser(key: String): UIO[Unit]

  def clearUsers(key: List[String]): UIO[Unit]

  def checkInactiveUsers: ZIO[Scope, Throwable, List[String]]

  def checkStorage(): UIO[Map[String, StorageUser]]
}

