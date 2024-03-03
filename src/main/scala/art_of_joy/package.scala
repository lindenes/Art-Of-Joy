import io.getquill.*

package object art_of_joy {
  val ctx = new PostgresZioJdbcContext(SnakeCase)
}
