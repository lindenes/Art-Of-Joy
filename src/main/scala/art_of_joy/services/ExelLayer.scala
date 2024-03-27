package art_of_joy.services

import art_of_joy.model.`enum`.ExelField
import art_of_joy.model.product
import art_of_joy.services.interfaces.ExelTrait
import org.apache.poi.ss.usermodel.*
import art_of_joy.model.product.{ExelProduct, Product}
import zio.*

import java.io.ByteArrayInputStream

object ExelLayer {
  val live = ZLayer.succeed(
    new ExelTrait {
      override def getProductFromExel(data: Array[Byte]): ZIO[Any, Throwable, List[ExelProduct]] = {
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
        ZIO.from(
          (
            for i <- 1 until sheet.getPhysicalNumberOfRows
              yield {
                val row = sheet.getRow(i)
                ExelProduct(
                  fieldPositions.find(_._1 == ExelField.article).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.name).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.description).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.subcategory).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.brand).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.articleWB).fold(None)((_, index) => Option(row.getCell(index)).map(_.getNumericCellValue.toInt.toString)),
                  fieldPositions.find(_._1 == ExelField.barcode).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.material).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.fragility).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.productCountry).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.color).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.height).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.width).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.size).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.ruSize).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue)),
                  fieldPositions.find(_._1 == ExelField.mediaFile).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue))
                )
              }
            ).toList
        )
      }
    }
  )
}
