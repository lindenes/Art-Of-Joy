package art_of_joy.domain.service

import art_of_joy.ctx
import art_of_joy.domain.model.Errors._
import art_of_joy._
import art_of_joy.domain.model.{Person, StoragePerson}
import art_of_joy.domain.service.EmailService
import art_of_joy.domain.service.session.SessionStorage
import art_of_joy.repository.model.PersonRow
import art_of_joy.repository.service.person.PersonTable
import art_of_joy.utils.*
import io.getquill.*
import zio.*

import java.util.{Date, UUID}
import javax.sql.DataSource

object PersonService {
  
  private def getRegistrationError(passwordValid: Boolean, emailValid: Boolean, emailChecker: Boolean, phoneChecker: Boolean) =
    List(
      (passwordValid, "password_regFormTI", RegistrationError.passwordValidationError),
      (emailValid, "email_regFormTI", RegistrationError.emailValidationError),
      (emailChecker, "email_regFormTI", RegistrationError.emailCheckerError),
      (phoneChecker, "phone_regFormTI", RegistrationError.phoneCheckerError)
    ).collect { case (false, fieldName, msg) => ValidationFields(fieldName, msg.message) }

  private def getPasswordError(repeatValid: Boolean, passwordValid: Boolean, isOldEqual: Boolean, oldEqualNewValid: Boolean) =
    List(
      (repeatValid, "passwordRepeat_userInfoFormTI", "Пароли не совпадают"),
      (passwordValid, "password_userInfoFormTI", RegistrationError.passwordValidationError.message),
      (isOldEqual, "oldPassword_userInfoFormTI", "Неверный старый пароль"),
      (oldEqualNewValid, "oldPassword_userInfoFormTI", "Новый пароль совпадает со старым")
    ).collect { case (false, fieldName, msg) => ValidationFields(fieldName, msg) }
  
  def getAllPersons(startRow: Int, endRow: Option[Int]): ZIO[DataSource & PersonTable, DomainError, List[Person]] =
    PersonTable.getAllPersons(startRow, endRow)
      .map(_.map(person => 
        Person(
          person.surname, person.email, person.phone, Role.fromOrdinal(person.role), 
          person.firstname, person.middleName, person.id, 
          person.passwordHash, person.isConfirmEmail, person.isConfirmPhone 
        )
      ))

  def emailRegistration(email: String): ZIO[Env, DomainError, String] =
    for {
      emailValid <- ZIO.succeed(isValidEmail(email))
      isEmailBusy <- PersonTable.getPersonByEmail(email).map(_.headOption.nonEmpty)
      result <- ZIO.ifZIO(ZIO.succeed(isEmailBusy))(
        onTrue = ZIO.fail(ValidationError(
          getRegistrationError(true, emailValid, !isEmailBusy, true)
        )),
        onFalse =
          for {
            sessionID <- ZIO.succeed(UUID.randomUUID.toString)
            acceptCode <- ZIO.succeed(generateCode)
            _ <- EmailService.sendMessage("Подтверждение почты", s"Ваш код подтверждения $acceptCode", email)
            _ <- SessionStorage.put(sessionID, StoragePerson(
              Person(None, email, None, Role.user, None,None, -1L, None, false, false),
              new Date().getTime, acceptCode
            ))
          } yield sessionID
            
      )
    } yield result

  def phoneRegistration(phone: String): ZIO[SessionStorage, DomainError, String] = ???

  def authPersonOnEmail(email: String): ZIO[DataSource & SessionStorage, DomainError, String] = ???

  def setPassword(personId: Long, newPassword: String, repeatPassword: String): ZIO[DataSource & PersonTable, DomainError, Long] =
    for {
      validationError <- ZIO.succeed(
        getPasswordError(
          newPassword == repeatPassword,
          isValidPassword(newPassword),
          true,
          true
        )
      )
      result <-
        if (validationError.isEmpty)
          PersonTable.setPersonData(personId, Option(passToHash(newPassword)))
        else
          ZIO.fail(ValidationError(validationError))
    } yield result
  

  def updatePassword(personId: Long, newPassword: String, repeatPassword: String, oldPassword: String): ZIO[DataSource & PersonTable, DomainError, Long] =
    for {
      dbPersonPass <- PersonTable.getPersonById(personId).map(_.head)
      newPassHash <- ZIO.succeed(passToHash(newPassword))
      validationError <- ZIO.succeed(
        getPasswordError(
          newPassword == repeatPassword,
          isValidPassword(newPassword),
          newPassHash == dbPersonPass.passwordHash.get,
          newPassword != oldPassword
        )
      )
      updatedData <-
        if (validationError.isEmpty)
          PersonTable.setPersonData(personId, Option(newPassHash))
        else
          ZIO.fail(ValidationError(validationError))
      result <- 
        if updatedData == 1 then ZIO.succeed(updatedData)
        else ZIO.fail(NotFoundError())
    } yield result

  def setPersonInfo(id: Long, surname: String, firstname: String, middleName: Option[String]): ZIO[DataSource & PersonTable, DomainError, Long] =
    for{
      updatedData <- PersonTable.setPersonData(id, None, Option(surname), Option(firstname), middleName)
      result <-
        if updatedData == 1 then ZIO.succeed(updatedData)
        else ZIO.fail(NotFoundError())
    }yield result
    
  def addPerson(person:Person) = 
    PersonTable.addPerson(person).map(person =>
      Person(
        person.surname, person.email, person.phone, Role.fromOrdinal(person.role),
        person.firstname, person.middleName, person.id, person.passwordHash,
        person.isConfirmEmail, person.isConfirmPhone
      )
    )
  
  def getPersonByEmail(email:String): ZIO[DataSource & PersonTable, DomainError, List[Person]] =
    PersonTable.getPersonByEmail(email)
      .map(_.map(person => 
        Person(
          person.surname, person.email, person.phone, Role.fromOrdinal(person.role),
          person.firstname, person.middleName, person.id, person.passwordHash,
          person.isConfirmEmail, person.isConfirmPhone
        )
      ))
  
  def authPerson(email:String, password:String) =
    PersonTable.getPersonByEmail(email).map(
      _.map(person =>
        Person(
          person.surname, person.email, person.phone, Role.fromOrdinal(person.role),
          person.firstname, person.middleName, person.id, person.passwordHash,
          person.isConfirmEmail, person.isConfirmPhone
        )
      ).filter(_.passwordHash.getOrElse("") == passToHash(password))
    )
}
