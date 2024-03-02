import io.getquill.*

package object lemyr {
  val ctx = new PostgresZioJdbcContext(SnakeCase)
}
