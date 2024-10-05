package art_of_joy.domain.service

import zio.*
import zio.config.*
import Config.*
import art_of_joy.ApplicationConfig.{SmtpConfig, smtpConfig}
import art_of_joy.domain.model.Errors.{DomainError, EmailServiceError}
import zio.config.magnolia.deriveConfig

import javax.mail.*
import javax.mail.internet.*
import java.util.Properties
import javax.mail

object EmailService {
  def sendMessage(title: String, body: String, emailReceiver: String): ZIO[SmtpConfig, DomainError, Unit] =
    (
      for {
        config <- ZIO.service[SmtpConfig]
        props <- ZIO.from {
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
        message <- ZIO.from {
          val message = new MimeMessage(session);
          message.setFrom(new InternetAddress(config.email));
          message.setRecipients(Message.RecipientType.TO, emailReceiver);
          message.setSubject(title);
          message.setText(body);
          message
        }
        _ <- ZIO.from(
          Transport.send(message)
        )
      } yield {}
    ).mapError(ex => EmailServiceError(ex.getMessage))
}
