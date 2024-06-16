package art_of_joy.http

import art_of_joy.model.`enum`._
import art_of_joy.model.http.*
import art_of_joy.model.person._
import art_of_joy.services.SessionStorageLayer.StoragePerson
import art_of_joy.services.interfaces.{PersonService, SessionStorageService}
import zio.ZIO
import sttp.tapir.ztapir.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import art_of_joy.utils.*
import zio.json.ast.{Json, JsonCursor}

import java.util.{Date, UUID}

object PersonRoute {
  val personRoute = List(
    endpoint.get
      .in("person")
      .in(token)
      .in(query[Int]("startRow"))
      .in(query[Option[Int]]("endRow"))
      .out(jsonBody[List[Person]])
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic((token, startRow, endRow) =>
        (
          for{
            _ <- SessionStorageService.updateTime(token)
            users <- PersonService.getAllPersons(startRow, endRow)
          }yield users
        ).mapError(err => HttpResponse(false, err.getMessage))
      ),
    endpoint.post
      .in("registration")
      .in(jsonBody[RegPerson])
      .out(
        oneOf(
          oneOfVariant(jsonBody[HttpResponse]),
          oneOfVariant(jsonBody[HttpValidationResponse])
        )
      )
      .out(header[String]("Token"))
      .out(header[String]("Access-Control-Expose-Headers"))
      .zServerLogic(regInfo =>
        (
          for{
            _ <- ZIO.when(regInfo.email.isEmpty && regInfo.phone.isEmpty)(ZIO.fail(new Exception("Не передана почта или номер телефона для регистрации")))
            callBack <- regInfo.email match
              case Some(value) => PersonService.emailRegistration(value.toLowerCase)
              case None => PersonService.phoneRegistration(regInfo.phone.get)
            response <- callBack match
              case Left(sessionID) =>
                SessionStorageService.get(sessionID).map{
                  case Some(storagePerson) => (HttpResponse(true, "Код подтверждения отправлен"), sessionID, "token")
                  case None => (HttpResponse(false, "Авторизируйтесь заново"), "", "")
                }
              case Right(errorList) => ZIO.from(
                (HttpValidationResponse(false, errorList),"","" )
              )
          }yield response
        ).mapError(err => HttpResponse(false, err.getMessage))
      ),
    endpoint.post
      .in("acceptCode")
      .in(jsonBody[AcceptCode])
      .in(header[String]("Token"))
      .out(
        oneOf(
          oneOfVariant(jsonBody[HttpResponse]),
          oneOfVariant(jsonBody[HttpValidationResponse]),
          oneOfVariant(jsonBody[Person])
        )
      )
      .zServerLogic((acceptCode, token) =>
        (
          for{
            _ <- SessionStorageService.updateTime(token)
            storagePerson <- SessionStorageService.get(token)
            response <- storagePerson match
              case Some(value) =>
                AcceptCodeType.fromOrdinal(acceptCode.acceptCodeType) match
                  case AcceptCodeType.registration =>
                      if (value.acceptCode == acceptCode.acceptCode)
                        for{
                          person <- PersonService.addPerson(value.person.copy(is_confirm_email = true))
                          _ <- SessionStorageService.updatePerson(token, person)
                          response <- ZIO.from(person)
                        }yield response
                      else
                        ZIO.from(
                          HttpValidationResponse(false, List(
                            HttpValidationFields("acceptCode", "Неверный код подтверждения")
                          ))
                        )
                  case AcceptCodeType.authorization =>
                    for{
                      email <- ZIO.fromOption(value.person.email).mapError(err => new Exception("В хранилище не найден email"))
                      person <- PersonService.getPersonByEmail(email)
                      _ <- ZIO.when(person.length > 1)(ZIO.fail(new Exception("С такой почтой несколько пользователей")))
                      response <- ZIO.from(person.head)
                    }yield response
              case None => ZIO.from(HttpResponse(false, "Авторизируйтесь заново"))
          }yield response
        ).mapError(err => HttpResponse(false, err.getMessage))
      ),
    endpoint.post
      .in("authorization")
      .in(jsonBody[AuthPerson])
      .in(header[Option[String]]("Token"))
      .out(
        oneOf(
          oneOfVariant(jsonBody[ClientPerson]),
          oneOfVariant(jsonBody[HttpValidationResponse]),
          oneOfVariant(jsonBody[HttpResponse])
        )
      )
      .out(header[String]("Token"))
      .out(header[String]("Access-Control-Expose-Headers"))
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic((clientPerson,token) =>
        (
          AuthType.fromOrdinal(clientPerson.authType) match
              case AuthType.passwordAuth =>
                for{
                  email <- ZIO.fromOption(clientPerson.email).mapError(err => new Exception("Не ввели почту"))
                  password <- ZIO.fromOption(clientPerson.password).mapError(err => new Exception("Не ввели пароль"))
                  _ <- ZIO.when(!isValidEmail(email))(ZIO.fail(HttpValidationFields("email_authFormTI", RegistrationError.emailValidationError.message)))
                  personByEmail <- PersonService.getPersonByEmail(email)
                  _ <- ZIO.when(personByEmail.isEmpty)(ZIO.fail(HttpValidationFields("email_authFormTI", "Пользователь с такой почтой не зарегистрирован")))
                  passwordByEmail <- PersonService.getPersonByEmail(email).map(_.head.password_hash)
                  _ <- ZIO.when(passwordByEmail.isEmpty)(ZIO.fail(HttpValidationFields("password_authFormTI","У пользователя не установлен пароль")))
                  _ <- ZIO.when(!isValidPassword(password))(ZIO.fail(HttpValidationFields("password_authFormTI",RegistrationError.passwordValidationError.message)))
                  person <- PersonService.authPerson(email,password)
                  token <- ZIO.from(UUID.randomUUID.toString)
                  _ <- SessionStorageService.put(token, StoragePerson(person.head, new Date().getTime))
                  result <- if person.isEmpty
                  then ZIO.from(
                      (HttpValidationResponse(false,
                        List(HttpValidationFields("email_authFormTI", "Пользователь с такой почтой не зарегистрирован"))
                      ),"", "")
                    )
                  else
                    ZIO.from(
                        (person.map( p =>
                          ClientPerson(p.surname, p.email,p.phone, p.role, p.firstname, p.middlename, p.is_confirm_email, p.is_confirm_phone, p.password_hash.nonEmpty)
                        ).head, token, "Token")
                    )
                }yield result
              case AuthType.emailAuth =>
                for{
                  email <- ZIO.fromOption(clientPerson.email).mapError(err => new Exception("Не ввели почту"))
                  token <- PersonService.authPersonOnEmail(email)
                  result <- ZIO.from(
                    (HttpResponse(true, "Код подтверждения отправлен"), "", "")
                  )
                }yield result
              case AuthType.phoneAuth => ZIO.from(
                (HttpResponse(false, "not available"), "", "")
              )
              case AuthType.tokenAuth =>
                for{
                  openToken <- ZIO.fromOption(token).mapError(err => new Exception("Не найден токен"))
                  user <- SessionStorageService.get(openToken)
                  result <- ZIO.fromOption(user).mapError(err => new Exception("Не найден пользователь"))
                  _ <- SessionStorageService.updateTime(openToken)
                }yield (result.person.toClientPerson, openToken, "Token")
        ).mapError(err => HttpResponse(false, err.getMessage))
      ),
    endpoint.post
      .in("password")
      .in(jsonBody[SetPassword])
      .in(header[String]("Token"))
      .out(
        oneOf(
          oneOfVariant(jsonBody[HttpResponse]),
          oneOfVariant(jsonBody[HttpValidationResponse])
        )
      )
      .errorOut(jsonBody[HttpResponse])
      .zServerLogic((setPassword,token) =>
        (
          for{
            _ <- SessionStorageService.updateTime(token)
            currentPerson <- SessionStorageService.get(token).flatMap(ZIO.fromOption(_)).mapError(ex => new Exception("Авторизируйтесь заново"))
            updatePass <- setPassword.oldPassword match
              case Some(password) => PersonService.updatePassword(
                currentPerson.person.id,
                setPassword.password,
                setPassword.repeatPassword,
                password
              )
              case None => PersonService.setPassword(
                currentPerson.person.id,
                setPassword.password,
                setPassword.repeatPassword
              )
            response <- updatePass match
              case Left(value) =>
                if value >= 1 then ZIO.from( HttpResponse(true, "Данные обновлены") )
                else ZIO.from( HttpResponse(false, "Не получилось найти нужного пользователя"))
              case Right(value) =>
                ZIO.from(HttpValidationResponse(false, value))
          }yield response
        ).mapError(ex =>  HttpResponse(false, ex.getMessage))
      ),
    endpoint.post
      .in("personInfo")
      .in(jsonBody[UpdatePersonInfo])
      .in(header[String]("Token"))
      .out(jsonBody[HttpResponse])
      .zServerLogic((updatePersonInfo, token) =>
        (
          for{
            _ <- SessionStorageService.updateTime(token)
            currentPerson <- SessionStorageService.get(token).flatMap(ZIO.fromOption(_)).mapError(err => new Exception("Авторизируйтесь заново"))
            updatedRows <- PersonService.setPersonInfo(
              currentPerson.person.id,
              updatePersonInfo.surname,
              updatePersonInfo.firstname,
              updatePersonInfo.middlename
            )
            response <- if updatedRows >= 1
            then ZIO.from(HttpResponse(true, "Данные обновлены"))
            else ZIO.from(HttpResponse(false, "Не получилось найти нужного пользователя"))
          }yield response
        ).mapError(ex =>  HttpResponse(false, ex.getMessage))
      )
  )
  val routes = ZioHttpInterpreter().toHttp(personRoute)
  val endPointList = personRoute.map(_.endpoint)
}
