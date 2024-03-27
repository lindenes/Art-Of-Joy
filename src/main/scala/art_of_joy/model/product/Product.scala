package art_of_joy.model.product

import java.util.Calendar

case class Product(id:Int,
                   article:String,
                   name:String,
                   description:String,
                   price:Double,
                   subcategory_id:Int,
                   brand_id:Int,
                   created_at: Calendar,
                   article_wb:String,
                   barcode:String,
                   material:String,
                   fragility:Boolean,
                   product_country:String,
                   color:String,
                   height:Double,
                   width:Double,
                   size:String,
                   ru_size:String
                  )
