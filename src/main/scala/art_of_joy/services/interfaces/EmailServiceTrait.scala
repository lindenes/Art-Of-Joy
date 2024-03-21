package art_of_joy.services.interfaces

import art_of_joy.model.SmtpConfig
import zio._
trait EmailServiceTrait {
  def getSmtpConfig:ZIO[Any, Throwable,SmtpConfig]
  def sendMessage(title:String, message:String,  emailReceiver:String):ZIO[Any,Throwable, Unit]
}
