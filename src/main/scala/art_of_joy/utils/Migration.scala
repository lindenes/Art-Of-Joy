package art_of_joy.utils

import zio.stream.{ZSink, ZStream}
import art_of_joy.ctx
import io.getquill.*
import io.getquill.context.ExecutionInfo
import zio.ZIO

object Migration {
  import ctx._
  def createTables =
    ZStream.fromInputStream(getClass.getResourceAsStream("/artofjoyDB.sql"))
      .split(_ == ';'.toInt)
      .map(chunk => new String(chunk.toArray))
      .foreach(command => ctx.executeAction(command)(ExecutionInfo.unknown, Long))
}
