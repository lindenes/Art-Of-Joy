package art_of_joy.model.product

import java.util.Calendar

case class Product(id:Int, name:String, description:String, subcategory_id:Int, brand_id:Int, price:BigDecimal, article:Int, created_at:Calendar)
