package art_of_joy.domain.model

import art_of_joy._

import java.sql.Timestamp

case class Person(
                   surname:Option[String],
                   email:String,
                   phone:Option[String],
                   role:Role,
                   firstname:Option[String],
                   middleName:Option[String],
                   id:Long,
                   passwordHash:Option[String],
                   isConfirmEmail:Boolean,
                   isConfirmPhone:Boolean
                 )

      