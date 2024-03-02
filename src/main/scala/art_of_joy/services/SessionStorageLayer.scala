package lemyr.services

import lemyr.model.person.Person
import lemyr.services.interfaces.SessionStorageTrait
import zio.*

import java.time.*
import java.util.Date
object SessionStorageLayer {

  case class StorageUser(person:Person, lastVisitTime:Long)

  private def checkExpired(userList: Map[String, StorageUser]):ZIO[Scope,Throwable, List[String]] = {
    val currentTime = new Date().getTime

    val expiredUsers = userList.collect {
      case (username, user) if (currentTime - user.lastVisitTime) > 3600000 => username
    }
    ZIO.succeed(expiredUsers.toList)
  }

  private def createRef[A] = Ref.make(Map.empty[String,A])

  val live =
    ZLayer {
      for{
        ref <- createRef[StorageUser]
      }yield {
        new SessionStorageTrait(ref) {
          override def get(key: String): ZIO[Any, Nothing, Option[StorageUser]] = ref.get.map(_.get(key))

          override def put(key: String, data: StorageUser): UIO[Unit] = ref.update(_.updated(key, data))

          override def checkStorage(): UIO[Map[String, StorageUser]] = ref.get

          override def updateTime(key: String): ZIO[Scope, Throwable, Unit] =
            for{
              storageUser <- get(key)
              user <- ZIO.fromOption(storageUser).mapError(err => new Exception("Not found user in storage"))
              updatedUser <- ZIO.from( user.copy(lastVisitTime = new Date().getTime) )
              storage <- ref.update(_.updated(key,updatedUser))
            }yield storage

          override def clearUser(key: String): UIO[Unit] = ref.update(_.-(key))

          override def clearUsers(key: List[String]): UIO[Unit] = ref.update(_.--(key))

          override def checkInactiveUsers: ZIO[Scope, Throwable, List[String]] =
            for{
              users <- ref.get
              inactiveUsers <- checkExpired(users)
            }yield inactiveUsers
        }
      }
    }
}