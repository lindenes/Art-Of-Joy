package art_of_joy.model.person

import java.util.Calendar

case class Address(
                 id:Int,
                 city:String,
                 street:String,
                 apartment_number:String,
                 created_at:Calendar,
                 person_id:Int
                 )
