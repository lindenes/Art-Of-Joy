package art_of_joy.model

case class SmtpConfig(host:String, port:String, username:String, password:String, auth:Boolean, startTls:Boolean, email:String)
