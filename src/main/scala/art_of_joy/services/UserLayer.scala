package art_of_joy.services

import art_of_joy.services.interfaces.UserTrait
import zio.{Scope, ZIO, ZLayer}
import io.getquill.*

import javax.sql.DataSource
import art_of_joy.ctx
import art_of_joy.model.`enum`.{RegistrationError, Role}
import art_of_joy.model.person.{Person, RegPerson}
import art_of_joy.utils.*
object UserLayer {
  import ctx._
  private def addPerson(email:String, password:String, number:String): ZIO[DataSource, Throwable, Person] =
    for {
      user <- ctx.run(
        quote {
          query[Person].insert(_.phone -> lift(number),
            _.role -> lift(Role.user.ordinal),
            _.email -> lift(email),
            _.password -> lift(Option(password)),
            _.is_confirm_email -> false,
            _.is_confirm_phone -> false
          ).returning(p => p)
        }
      )
    } yield user

  private def getRegistrationError(passwordValid:Boolean, emailValid:Boolean, emailChecker:Boolean,numberChecker:Boolean) =
    List(
      (passwordValid, RegistrationError.passwordValidationError),
      (emailValid, RegistrationError.emailValidationError),
      (emailChecker, RegistrationError.emailCheckerError),
      (numberChecker, RegistrationError.numberCheckerError)
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

      override def authUser(email: String, password: String): ZIO[DataSource, Throwable, List[Person]] =
        for{
          user <- ctx.run(
            quote{
              query[Person].filter(p => p.email == lift(email) && p.password.getOrElse("") == lift(passToHash(password)))
            }
          )
        }yield user

      override def checkEmail(email: String): ZIO[DataSource, Throwable, Boolean] =
        for{
          user <- ctx.run(
            quote{
              query[Person].filter(p => p.email == lift(email) )
            }
          )
        }yield if user.isEmpty
          then true
        else false

      override def checkNumber(number: String): ZIO[DataSource, Throwable, Boolean] =
        for {
          user <- ctx.run(
            quote {
              query[Person].filter(p => p.phone == lift(number))
            }
          )
        } yield if user.isEmpty
          then true
        else false

      override def passwordRegistration(email: String, password: String, number: String): ZIO[DataSource, Throwable, Either[Person, List[String]]] =
        for{
          passAndEmailValid <- ZIO.from(isValidPassword(password)) zipPar ZIO.from(isValidEmail(email))
          checkNumberAndEmail <- checkEmail(email) zipPar checkNumber(number)
          callBack <- ZIO.ifZIO(checkNumber(number) && checkEmail(email))(
            onTrue = addPerson(email, passToHash(password), number).map(Left(_)),
            onFalse = ZIO.from(
              getRegistrationError(passAndEmailValid._1, passAndEmailValid._2, checkNumberAndEmail._1, checkNumberAndEmail._2)
            ).map(Right(_))
          )
        }yield callBack

      override def emailRegistration(email: String, number: String): ZIO[DataSource, Throwable, Either[Person, List[String]]] = ???
    }
  )
}
