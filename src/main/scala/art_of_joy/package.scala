import art_of_joy.domain.service.session.SessionStorage
import art_of_joy.repository.service.category.CategoryTable
import art_of_joy.repository.service.person.PersonTable
import io.getquill.*
import sttp.tapir.*
import zio.Scope

import java.sql.Timestamp
import javax.sql.DataSource

package object art_of_joy {
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  implicit val timestampSchema: Schema[Timestamp] = Schema.string[Timestamp]
  
  type Env = DataSource & PersonTable & SessionStorage & CategoryTable
}
