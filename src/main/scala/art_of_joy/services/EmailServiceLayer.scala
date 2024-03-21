package art_of_joy.services
import art_of_joy.model.SmtpConfig
import art_of_joy.services.interfaces.EmailServiceTrait
import zio.*
import zio.config.*
import Config.*
import zio.config.magnolia.deriveConfig

import javax.mail.*
import javax.mail.internet.*
import java.util.Properties
import javax.mail
object EmailServiceLayer {
  val live = ZLayer.succeed(
    new EmailServiceTrait {
      override def getSmtpConfig: ZIO[Any, Throwable, SmtpConfig] = ZIO.config[SmtpConfig](deriveConfig[SmtpConfig].nested("SmtpConfig"))

      override def sendMessage(title: String, message: String, emailReceiver:String): ZIO[Any, Throwable, Unit] =
        for{
          config <- getSmtpConfig
          props <- ZIO.from{
            val props = new Properties()
            props.put("mail.smtp.auth", config.auth);
            props.put("mail.smtp.starttls.enable", config.startTls);
            props.put("mail.smtp.host", config.host);
            props.put("mail.smtp.port", config.port);
            props
          }
          session <- ZIO.from(
            Session.getInstance(props, new javax.mail.Authenticator() {
              override protected def getPasswordAuthentication: PasswordAuthentication = {
                new PasswordAuthentication(config.username, config.password)
              }
            })
          )
          message <- ZIO.from{
            val message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.email));
            message.setRecipients(Message.RecipientType.TO, emailReceiver);
            message.setSubject("Тестовое сообщение");
            message.setText("Это тестовое сообщение, отправленное через JavaMail.");
            message
          }
          _ <- ZIO.from(
            Transport.send(message)
          )
        }yield{}
    }
  )
}
