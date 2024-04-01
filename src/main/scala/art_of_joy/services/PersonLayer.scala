package art_of_joy.services

import art_of_joy.services.interfaces.{EmailServiceTrait, PersonTrait, SessionStorageTrait}
import zio.{Scope, ZIO, ZLayer}
import io.getquill.*

import javax.sql.DataSource
import art_of_joy.ctx
import art_of_joy.model.`enum`.{RegistrationError, Role, SetPasswordError}
import art_of_joy.model.http.{HttpResponse, HttpValidationFields}
import art_of_joy.model.person.{SetPassword, Person, RegPerson}
import art_of_joy.services.SessionStorageLayer.StoragePerson
import art_of_joy.utils.*

import java.util.{Date, UUID}
object PersonLayer {
  import ctx._
  private def addPerson(email:String, password:String, phone:String): ZIO[DataSource, Throwable, Person] =
    for {
      user <- ctx.run(
        quote {
          query[Person].insert(_.phone -> lift(Option(phone)),
            _.role -> lift(Role.user.ordinal),
            _.email -> lift(Option(email)),
            _.password_hash -> lift(Option(password)),
            _.is_confirm_email -> false,
            _.is_confirm_phone -> false
          ).returning(p => p)
        }
      )
    } yield user

  private def getRegistrationError(passwordValid:Boolean, emailValid:Boolean, emailChecker:Boolean,phoneChecker:Boolean) =
    List(
      (passwordValid,"password", RegistrationError.passwordValidationError),
      (emailValid,"email", RegistrationError.emailValidationError),
      (emailChecker,"email", RegistrationError.emailCheckerError),
      (phoneChecker,"phone", RegistrationError.phoneCheckerError)
    ).collect{ case (false, fieldName, msg) => HttpValidationFields(fieldName, msg.message)}
  private def getPassword(setPassword: SetPassword):List[HttpValidationFields] = ???
//    List(
//      (isValidPassword(setPassword.password), SetPasswordError.passwordValidationError.message),
//      (setPassword.repeatPassword == setPassword.password, SetPasswordError.notEqual)
//    )

  val live = ZLayer.succeed(
    new PersonTrait {
      override def getAllPersons(startRow:Int, endRow:Option[Int]): ZIO[DataSource, Throwable, List[Person]] =
        for{
          users <- endRow match
            case Some(value) =>
              for{
                _ <- ZIO.when(value < startRow)(ZIO.fail(new Exception("endRow должен быть больше startRow")))
                users <- ctx.run(
                  quote{
                    query[Person].drop(lift(startRow)).take(lift(value))
                  }
                )
              }yield users
            case None => ctx.run(
              quote{
                query[Person]
              }
            )
        }yield users

      override def getPersonByEmail(email:String): ZIO[DataSource, Throwable, List[Person]] =
        for{
          user <- ctx.run(
            quote{
              query[Person].filter(p => 
                p.email == lift(Option(email))
              )
            }
          )
        }yield user

      override def authPerson(email: String, password: String): ZIO[DataSource, Throwable, List[Person]] =
        for{
          user <- ctx.run(
            quote{
              query[Person].filter(p => p.email == lift(Option(email)) && p.password_hash.getOrElse("") == lift(passToHash(password)))
            }
          )
        }yield user

      override def checkEmail(email: String): ZIO[DataSource, Throwable, Boolean] =
        for{
          user <- ctx.run(
            quote{
              query[Person].filter(p => p.email == lift(Option(email)) )
            }
          )
        }yield user.isEmpty


      override def checkPhone(phone: String): ZIO[DataSource, Throwable, Boolean] =
        for {
          user <- ctx.run(
            quote {
              query[Person].filter(p => p.phone == lift(Option(phone)))
            }
          )
        } yield user.isEmpty

      override def emailRegistration(email: String): ZIO[SessionStorageTrait & DataSource & EmailServiceTrait, Throwable, Either[String, List[HttpValidationFields]]] =
        for{
          emailValid <- ZIO.from(isValidEmail(email))
          isEmailBusy <- checkEmail(email)
          result <- ZIO.ifZIO(ZIO.from(!isEmailBusy))(
            onTrue =ZIO.from(
              getRegistrationError(true, emailValid, isEmailBusy, true)
            ).map(Right(_)),
            onFalse =
              (
                for{
                  storage <- ZIO.service[SessionStorageTrait]
                  sessionID <- ZIO.from(
                    UUID.randomUUID.toString
                  )
                  acceptCode <- ZIO.succeed(generateCode)
                  emailService <- ZIO.service[EmailServiceTrait]
                  _ <- emailService.sendMessage("Подтверждение почты", s"Ваш код подтверждения $acceptCode", email)
                  _ <- storage.put(sessionID, StoragePerson(
                    Person(id = -1, role = Role.user.ordinal, is_confirm_email = false, is_confirm_phone = false),
                    new Date().getTime, Option(acceptCode)
                  ))
                }yield sessionID
              ).map(Left(_))
          )
        }yield result

      override def phoneRegistration(phone: String): ZIO[SessionStorageTrait, Throwable, Either[String, List[HttpValidationFields]]] = ???

      override def addPerson(person: Person): ZIO[DataSource, Throwable, Person] =
        for {
          user <- ctx.run(
            quote {
              query[Person].insert(
                _.role -> lift(person.role),
                _.phone -> lift(person.phone),
                _.email -> lift(person.email),
                _.surname -> lift(person.surname),
                _.firstname -> lift(person.firstname),
                _.middlename -> lift(person.middlename),
                _.is_confirm_email -> lift(person.is_confirm_email),
                _.is_confirm_phone -> lift(person.is_confirm_phone),
              ).returning(p => p)
            }
          )
        } yield user

      override def authUserOnEmail(email: String): ZIO[DataSource & SessionStorageTrait, Throwable, String] =
        for{
          isRegistration <- checkEmail(email)
          _ <- ZIO.when(isRegistration)(ZIO.fail(new Exception("Пользователь с такой почтой не зарегистрирован")))
          service <- ZIO.service[SessionStorageTrait]
          token <- ZIO.from(
            UUID.randomUUID.toString
          )
          _ <- service.put(token, StoragePerson(
            Person(email = Option(email)), new Date().getTime, Option(generateCode)
          ))
        }yield token

      override def setPassword(personID:Int, password: SetPassword): ZIO[DataSource, Throwable, HttpResponse] = ???
    }
  )
}
