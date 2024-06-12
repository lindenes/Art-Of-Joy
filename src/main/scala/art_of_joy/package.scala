import io.getquill.*
import sttp.tapir._

import java.sql.Timestamp

package object art_of_joy {
  val ctx = new PostgresZioJdbcContext(SnakeCase)
  implicit val timestampSchema: Schema[Timestamp] = Schema.string[Timestamp]
}
