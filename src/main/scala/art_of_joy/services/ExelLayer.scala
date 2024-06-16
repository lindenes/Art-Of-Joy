package art_of_joy.services

import art_of_joy.model.`enum`.ExelField
import art_of_joy.model.product
import art_of_joy.services.interfaces.ExelService
import org.apache.poi.ss.usermodel.*
import art_of_joy.model.product.{ExelProduct, Product}
import zio.*
import zio.http.{Request, ZClient}

import java.io.ByteArrayInputStream
import java.util.Base64

object ExelLayer {
  val live = ZLayer.succeed(
    new ExelService {
      override def getProductFromExel(data: Array[Byte]): ZIO[http.Client & Scope, Throwable, List[ExelProduct]] = {
        val inputStream = new ByteArrayInputStream(data)
        val sheet = WorkbookFactory.create(inputStream).getSheetAt(0)
        inputStream.close()
        val fieldPositions =
          (for index <- 0 until sheet.getRow(0).getPhysicalNumberOfCells
            yield {
              sheet.getRow(0).getCell(index).getStringCellValue -> index
            })
            .collect {
              case (value, index) if ExelField.values.exists(_.fieldName == value) => ExelField.values.find(_.fieldName == value).get -> index
            }
        ZIO.collectAll(
          (
            for i <- 1 until sheet.getPhysicalNumberOfRows yield sheet.getRow(i)
          ).map{ row =>
            for{
              media <- fieldPositions.find(_._1 == ExelField.mediaFile).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue.split(";"))) match
                case None => ZIO.from(Array.empty[String])
                case Some(value) => loadImage(value)
            }yield ExelProduct(
              fieldPositions.find(_._1 == ExelField.article).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.name).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.description).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.subcategory).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.brand).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.articleWB).fold(None)((_, index) => Option(row.getCell(index)).map(_.getNumericCellValue.toInt.toString)),
              fieldPositions.find(_._1 == ExelField.barcode).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.material).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.fragility).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue == "хрупкое")),
              fieldPositions.find(_._1 == ExelField.productCountry).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.color).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.height).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.width).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.size).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              fieldPositions.find(_._1 == ExelField.ruSize).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
              media
            )
          }
        ).map(_.toList)
      }

      override def loadImage(imageUrl: Array[String]):ZIO[http.Client & Scope, Throwable, Array[String]] =
          ZIO.collectAllPar(imageUrl.map(url => 
              for{
                response <- ZClient.request(Request.get(url))
                body <- response.body.asArray.map(Base64.getEncoder.encodeToString)
              }yield body
            )
          )
       
    }
  )
}
