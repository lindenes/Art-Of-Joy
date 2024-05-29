package art_of_joy.utils

import zio.stream.{ZSink, ZStream}
import art_of_joy.ctx
import io.getquill.*
import io.getquill.context.ExecutionInfo
import zio.ZIO

object Migration {
  import ctx._
  def createTables =
    for{
      commands <- getCreateTablesCommands
      responseData <- ZIO.foreach(commands)(command => ctx.executeAction(command)(ExecutionInfo.unknown, Long))
    }yield responseData

  def getCreateTablesCommands =
    ZStream.fromInputStream(getClass.getResourceAsStream("/artofjoyDB.sql"))
      .run(ZSink.collectAll)
      .map(_.toArray)
      .map(new String(_).split(";").map(_.trim))
}
