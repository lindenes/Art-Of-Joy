package art_of_joy.domain.service.email

import zio._

trait Email {
  def sendMessage(title:String, body:String,  emailReceiver:String):ZIO[Any,Throwable, Unit]
}
object Email{
  def sendMessage(title:String, body:String,  emailReceiver:String) =
    ZIO.serviceWithZIO[Email](_.sendMessage(title,body,emailReceiver))
}