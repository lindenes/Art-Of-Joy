package art_of_joy.services.interfaces

import art_of_joy.model.SmtpConfig
import zio._
trait EmailService {
  def getSmtpConfig:ZIO[Any, Throwable,SmtpConfig]
  def sendMessage(title:String, body:String,  emailReceiver:String):ZIO[Any,Throwable, Unit]
}
object EmailService{
  def smtpConfig =
    ZIO.serviceWithZIO[EmailService](_.getSmtpConfig)
  def sendMessage(title:String, body:String,  emailReceiver:String) =
    ZIO.serviceWithZIO[EmailService](_.sendMessage(title,body,emailReceiver))
}