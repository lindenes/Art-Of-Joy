package art_of_joy.domain.model


import art_of_joy.repository.model.PersonRow

case class StoragePerson(person: PersonRow, lastVisitTime: Long, acceptCode: String = "")
