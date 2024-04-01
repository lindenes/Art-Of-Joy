package art_of_joy.services

import art_of_joy.services.interfaces.{EmailServiceTrait, SessionStorageTrait, UserTrait}
import zio.{Scope, ZIO, ZLayer}
import io.getquill.*

import javax.sql.DataSource
import art_of_joy.ctx
import art_of_joy.model.`enum`.{RegistrationError, Role}
import art_of_joy.model.person.{Person, RegPerson}
import art_of_joy.services.SessionStorageLayer.StorageUser
import art_of_joy.utils.*

import java.util.{Date, UUID}
object UserLayer {
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
      (passwordValid, RegistrationError.passwordValidationError),
      (emailValid, RegistrationError.emailValidationError),
      (emailChecker, RegistrationError.emailCheckerError),
      (phoneChecker, RegistrationError.phoneCheckerError)
    ).collect{ case (false, msg) => msg.message}

  val live = ZLayer.succeed(
    new UserTrait {
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

      override def authUser(email: String, password: String): ZIO[DataSource, Throwable, List[Person]] =
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

      override def emailRegistration(email: String): ZIO[SessionStorageTrait & DataSource & EmailServiceTrait, Throwable, Either[String, List[String]]] =
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
                  _ <- storage.put(sessionID, StorageUser(
                    Person(id = -1, role = Role.user.ordinal, is_confirm_email = false, is_confirm_phone = false),
                    new Date().getTime, Option(acceptCode)
                  ))
                }yield sessionID
              ).map(Left(_))
          )
        }yield result

      override def phoneRegistration(phone: String): ZIO[SessionStorageTrait, Throwable, Either[String, List[String]]] = ???

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
          _ <- service.put(token, StorageUser(
            Person(email = Option(email)), new Date().getTime, Option(generateCode)
          ))
        }yield token
    }
  )
}
