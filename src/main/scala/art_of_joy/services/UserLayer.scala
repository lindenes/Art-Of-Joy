package art_of_joy.services

import art_of_joy.services.interfaces.UserTrait
import zio.{Scope, ZIO, ZLayer}
import io.getquill.*
import javax.sql.DataSource
import art_of_joy.ctx
import art_of_joy.model.person.Person
import art_of_joy.utils._
object UserLayer {

  import ctx._

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
              query[Person].filter(p => p.email == lift(email) && p.password == lift(passToHash(password)))
            }
          )
        }yield user
    }
  )
}
