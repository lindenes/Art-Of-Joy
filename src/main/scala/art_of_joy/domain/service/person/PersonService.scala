package art_of_joy.domain.service.person

import art_of_joy.ctx
import art_of_joy.domain.model.StoragePerson
import art_of_joy.domain.service.email.Email
import art_of_joy.domain.service.session.SessionStorage
import zio.*
import io.getquill.*
import art_of_joy.model.`enum`.{RegistrationError, Role, SetPasswordError}
import art_of_joy.model.http.{HttpResponse, HttpValidationFields}
import art_of_joy.model.person.{RegPerson, SetPassword}
import art_of_joy.repository.model.PersonRow
import art_of_joy.utils.*

import java.util.UUID
import javax.sql.DataSource
import java.util.Date
class PersonService extends Person{
  import ctx._

  private def addPerson(email: String, password: String, phone: String): ZIO[DataSource, Throwable, PersonRow] =
    for {
      user <- ctx.run(
        quote {
          query[PersonRow].insert(_.phone -> lift(Option(phone)),
            _.role -> lift(Role.user.ordinal),
            _.email -> lift(Option(email)),
            _.password_hash -> lift(Option(password)),
            _.is_confirm_email -> false,
            _.is_confirm_phone -> false
          ).returning(p => p)
        }
      )
    } yield user

  private def getRegistrationError(passwordValid: Boolean, emailValid: Boolean, emailChecker: Boolean, phoneChecker: Boolean) =
    List(
      (passwordValid, "password_regFormTI", RegistrationError.passwordValidationError),
      (emailValid, "email_regFormTI", RegistrationError.emailValidationError),
      (emailChecker, "email_regFormTI", RegistrationError.emailCheckerError),
      (phoneChecker, "phone_regFormTI", RegistrationError.phoneCheckerError)
    ).collect { case (false, fieldName, msg) => HttpValidationFields(fieldName, msg.message) }

  private def getPasswordError(repeatValid: Boolean, passwordValid: Boolean, isOldEqual: Boolean, oldEqualNewValid: Boolean) =
    List(
      (repeatValid, "passwordRepeat_userInfoFormTI", "Пароли не совпадают"),
      (passwordValid, "password_userInfoFormTI", RegistrationError.passwordValidationError.message),
      (isOldEqual, "oldPassword_userInfoFormTI", "Неверный старый пароль"),
      (oldEqualNewValid, "oldPassword_userInfoFormTI", "Новый пароль совпадает со старым")
    ).collect { case (false, fieldName, msg) => HttpValidationFields(fieldName, msg) }

  private def getPassword(setPassword: SetPassword): List[HttpValidationFields] = ???
  
  override def getAllPersons(startRow: Int, endRow: Option[Int]): ZIO[DataSource, Throwable, List[PersonRow]] =
    for {
      users <- endRow match
        case Some(value) =>
          for {
            _ <- ZIO.when(value < startRow)(ZIO.fail(new Exception("endRow должен быть больше startRow")))
            users <- ctx.run(
              quote {
                query[PersonRow].drop(lift(startRow)).take(lift(value))
              }
            )
          } yield users
        case None => ctx.run(
          quote {
            query[PersonRow]
          }
        )
    } yield users

  override def getPersonByEmail(email: String): ZIO[DataSource, Throwable, List[PersonRow]] =
    ctx.run(
      quote {
        query[PersonRow].filter(p =>
          p.email == lift(Option(email))
        )
      }
    )

  override def authPerson(email: String, password: String): ZIO[DataSource, Throwable, List[PersonRow]] =
    ctx.run(
      quote {
        query[PersonRow].filter(p => p.email == lift(Option(email)) && p.password_hash.getOrElse("") == lift(passToHash(password)))
      }
    )

  override def checkEmail(email: String): ZIO[DataSource, Throwable, Boolean] =
    ctx.run(
      quote {
        query[PersonRow].filter(p => p.email == lift(Option(email)))
      }
    ).map(_.isEmpty)


  override def checkPhone(phone: String): ZIO[DataSource, Throwable, Boolean] =
    ctx.run(
      quote {
        query[PersonRow].filter(p => p.phone == lift(Option(phone)))
      }
    ).map(_.isEmpty)

  override def emailRegistration(email: String): ZIO[SessionStorage & DataSource & Email, Throwable, Either[String, List[HttpValidationFields]]] =
    for {
      emailValid <- ZIO.from(isValidEmail(email))
      isEmailBusy <- checkEmail(email)
      result <- ZIO.ifZIO(ZIO.from(!isEmailBusy))(
        onTrue = ZIO.from(
          getRegistrationError(true, emailValid, isEmailBusy, true)
        ).map(Right(_)),
        onFalse =
          (
            for {
              sessionID <- ZIO.from(UUID.randomUUID.toString)
              acceptCode <- ZIO.succeed(generateCode)
              _ <- Email.sendMessage("Подтверждение почты", s"Ваш код подтверждения $acceptCode", email)
              _ <- SessionStorage.put(sessionID, StoragePerson(
                PersonRow(id = -1, email = Option(email), role = Role.user.ordinal, is_confirm_email = false, is_confirm_phone = false),
                new Date().getTime, acceptCode
              ))
            } yield sessionID
            ).map(Left(_))
      )
    } yield result

  override def phoneRegistration(phone: String): ZIO[SessionStorage, Throwable, Either[String, List[HttpValidationFields]]] = ???

  override def addPerson(person: PersonRow): ZIO[DataSource, Throwable, PersonRow] =
    ctx.run(
      quote {
        query[PersonRow].insert(
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

  override def authPersonOnEmail(email: String): ZIO[DataSource & SessionStorage, Throwable, String] =
    for {
      isRegistration <- checkEmail(email)
      _ <- ZIO.when(isRegistration)(ZIO.fail(new Exception("Пользователь с такой почтой не зарегистрирован")))
      token <- ZIO.from(
        UUID.randomUUID.toString
      )
      _ <- SessionStorage.put(token, StoragePerson(
        PersonRow(email = Option(email)), new Date().getTime, generateCode
      ))
    } yield token

  override def setPassword(personID: Int, newPassword: String, repeatPassword: String): ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]] =
    for {
      validationError <- ZIO.from(
        getPasswordError(
          newPassword == repeatPassword,
          isValidPassword(newPassword),
          true,
          true
        )
      )
      result <-
        if (validationError.isEmpty)
          ctx.run(
            quote {
              query[PersonRow].filter(_.id == lift(personID)).update(_.password_hash -> lift(Option(passToHash(newPassword))))
            }
          ).map(Left(_))
        else
          ZIO.from(validationError).map(Right(_))
    } yield result

  override def getPersonByID(personID: Long): ZIO[DataSource, Throwable, PersonRow] =
    ctx.run(
      quote {
        query[PersonRow].filter(_.id == lift(personID))
      }
    ).map(_.head)

  override def updatePassword(personID: Int, newPassword: String, repeatPassword: String, oldPassword: String): ZIO[DataSource, Throwable, Either[Long, List[HttpValidationFields]]] =
    for {
      dbPersonPass <- getPersonByID(personID)
      newPassHash <- ZIO.from(passToHash(newPassword))
      validationError <- ZIO.from(
        getPasswordError(
          newPassword == repeatPassword,
          isValidPassword(newPassword),
          newPassHash == dbPersonPass.password_hash.get,
          newPassword != oldPassword
        )
      )
      result <-
        if (validationError.isEmpty)
          ctx.run(
            quote {
              query[PersonRow].filter(_.id == lift(personID)).update(_.password_hash -> lift(Option(newPassHash)))
            }
          ).map(Left(_))
        else
          ZIO.from(validationError).map(Right(_))
    } yield result

  override def setPersonInfo(id: Long, surname: String, firstname: String, middleName: Option[String]): ZIO[DataSource, Throwable, Long] =
    ctx.run(
      dynamicQuery[PersonRow]
        .filter(_.id == lift(id))
        .update(
          setValue(_.firstname, Option(firstname)),
          setValue(_.surname, Option(surname)),
          setOpt(_.middlename, middleName match
            case Some(value) => Option(Option(value))
            case None => Option.empty[Option[String]]
          )
        )
    )
}
object PersonService{
  val live = ZLayer.succeed(PersonService())
}
