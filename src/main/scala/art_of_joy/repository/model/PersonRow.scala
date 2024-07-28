package art_of_joy.repository.model

import art_of_joy._
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.sql.Timestamp

case class PersonRow(surname:Option[String],
                     email:String,
                     phone:Option[String],
                     role:Int,
                     firstname:Option[String],
                     middleName:Option[String],
                     id:Long,
                     passwordHash:Option[String],
                     isConfirmEmail:Boolean,
                     isConfirmPhone:Boolean,
                     createdAt:Timestamp
                    )
