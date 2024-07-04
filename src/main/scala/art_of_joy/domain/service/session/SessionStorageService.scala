package art_of_joy.domain.service.session
import art_of_joy.domain.model.StoragePerson
import art_of_joy.repository.model.PersonRow
import zio.*

import java.util.Date

class SessionStorageService(ref:Ref[Map[String, StoragePerson]]) extends SessionStorage {

  private def checkExpired(userList: Map[String, StoragePerson]): ZIO[Scope, Throwable, List[String]] = {
    val currentTime = new Date().getTime

    val expiredUsers = userList.collect {
      case (username, user) if (currentTime - user.lastVisitTime) > 3600000 => username
    }
    ZIO.succeed(expiredUsers.toList)
  }

  override def get(key: String): ZIO[Any, Nothing, Option[StoragePerson]] = ref.get.map(_.get(key))

  override def put(key: String, data: StoragePerson): UIO[Unit] = ref.update(_.updated(key, data))

  override def checkStorage: UIO[Map[String, StoragePerson]] = ref.get

  override def updateTime(key: String): ZIO[Scope, Throwable, Unit] =
    for {
      storagePerson <- get(key)
      user <- ZIO.fromOption(storagePerson).mapError(err => new Exception("Not found user in storage"))
      updatedUser <- ZIO.from(user.copy(lastVisitTime = new Date().getTime))
      storage <- ref.update(_.updated(key, updatedUser))
    } yield storage

  override def clearPerson(key: String): UIO[Unit] = ref.update(_.-(key))

  override def clearPersons(key: List[String]): UIO[Unit] = ref.update(_.--(key))

  override def checkInactivePersons: ZIO[Scope, Throwable, List[String]] =
    for {
      persons <- ref.get
      inactiveUsers <- checkExpired(persons)
    } yield inactiveUsers

  override def setAcceptCode(key: String, code: String): ZIO[Scope, Throwable, Unit] =
    for {
      storagePerson <- get(key)
      user <- ZIO.fromOption(storagePerson).mapError(err => new Exception("Not found user in storage"))
      updatedPerson <- ZIO.from(user.copy(acceptCode = code))
      storage <- ref.update(_.updated(key, updatedPerson))
    } yield storage

  override def clearAcceptCode(key: String): ZIO[Scope, Throwable, Unit] =
    for {
      storagePerson <- get(key)
      user <- ZIO.fromOption(storagePerson).mapError(err => new Exception("Not found user in storage"))
      updatedPerson <- ZIO.from(user.copy(acceptCode = ""))
      storage <- ref.update(_.updated(key, updatedPerson))
    } yield storage

  override def updatePerson(key: String, person: PersonRow): ZIO[Scope, Throwable, Unit] =
    for {
      storagePerson <- get(key)
      user <- ZIO.fromOption(storagePerson).mapError(err => new Exception("Not found user in storage"))
      updatedPerson <- ZIO.from(user.copy(person = person))
      storage <- ref.update(_.updated(key, updatedPerson))
    } yield storage
}
object SessionStorageService{
  val live = ZLayer{
    Ref.make(Map.empty[String, StoragePerson]).map(SessionStorageService(_))
  }
}
