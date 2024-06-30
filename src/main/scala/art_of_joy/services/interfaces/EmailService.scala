package art_of_joy.services.interfaces

import art_of_joy.config.ApplicationConfig.SmtpConfig
import zio.*
trait EmailService {
  def sendMessage(title:String, body:String,  emailReceiver:String):ZIO[Any,Throwable, Unit]
}
object EmailService{
  def sendMessage(title:String, body:String,  emailReceiver:String) =
    ZIO.serviceWithZIO[EmailService](_.sendMessage(title,body,emailReceiver))
}