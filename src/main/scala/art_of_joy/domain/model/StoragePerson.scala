package art_of_joy.domain.model


case class StoragePerson(person: Person, lastVisitTime: Long, acceptCode: String = "")
