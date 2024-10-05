package art_of_joy.repository.service.person

import art_of_joy.ctx
import art_of_joy.domain.model.Errors.{DataBaseError, DomainError}
import art_of_joy.domain.model.Person
import art_of_joy._
import art_of_joy.repository.model.PersonRow
import art_of_joy.repository.personSchema
import zio.*
import io.getquill.*

import javax.sql.DataSource

class PersonTableService extends PersonTable {
  import ctx._
  override def getPersonByEmail(email: String): ZIO[DataSource, DomainError, List[PersonRow]] =
    ctx.run(
      personSchema.filter(_.email == lift(email))
    ).mapError(ex => DataBaseError())

  override def getPersonByPhone(number: String): ZIO[DataSource, DomainError, List[PersonRow]] =
    ctx.run(
      personSchema.filter(_.phone == lift(Option(number)))
    ).mapError(ex => DataBaseError())

  override def getPersonById(personId: Long): ZIO[DataSource, DomainError, List[PersonRow]] =
    ctx.run(
      personSchema.filter(_.id == lift(personId))
    ).mapError(ex => DataBaseError())

  override def addPerson(person: Person): ZIO[DataSource, DomainError, PersonRow] = 
  ctx.run(
    quote {
      personSchema.insert(_.phone -> lift(person.phone),
        _.role -> lift(Role.user.ordinal),
        _.email -> lift(person.email),
        _.passwordHash -> lift(person.passwordHash),
        _.isConfirmEmail -> lift(person.isConfirmEmail),
        _.isConfirmPhone -> false
      ).returning(p => p)
    }
  ).mapError(ex => DataBaseError())

  override def getAllPersons(startRow: Int, endRow: Option[Int]): ZIO[DataSource, DomainError, List[PersonRow]] =
    (
      endRow match
        case Some(value) =>
          for {
            _ <- ZIO.when(value < startRow)(ZIO.fail(new Exception("endRow должен быть больше startRow")))
            users <- ctx.run(
              quote {
                personSchema.drop(lift(startRow)).take(lift(value))
              }
            )
          } yield users
        case None => ctx.run(
          quote {
            personSchema
          }
        )
    ).mapError(ex => DataBaseError())
      
  def setPersonData(id:Long, passwordHash:Option[String], surname:Option[String], firstname:Option[String], middleName:Option[String]): ZIO[DataSource, DomainError, Long] = ctx.run(
    dynamicQuerySchema[PersonRow](
      "person",
      alias(_.passwordHash, "password_hash"),
      alias(_.createdAt, "created_at"),
      alias(_.isConfirmEmail, "is_confirm_email"),
      alias(_.isConfirmPhone, "is_confirm_phone"),
      alias(_.middleName, "middlename")
    )
      .filter(_.id == lift(id))
      .update(
        setOpt[PersonRow,String](p => quote(p.passwordHash.getOrElse("")), passwordHash),
        setOpt[PersonRow,String](p => quote(p.firstname.getOrElse("")), firstname),
        setOpt[PersonRow,String](p => quote(p.surname.getOrElse("")), surname),
        setOpt[PersonRow,String](p => quote(p.middleName.getOrElse("")), middleName)
      )
  ).mapError(ex => DataBaseError())
  
}
object PersonTableService{
  val live = ZLayer.succeed(PersonTableService())
}