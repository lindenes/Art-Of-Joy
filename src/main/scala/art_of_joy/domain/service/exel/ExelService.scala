package art_of_joy.domain.service.exel

import art_of_joy.domain.model.Errors.{DomainError, LoadImageError}
import art_of_joy.domain.model.ExelProduct
import art_of_joy._
import zio.*
import zio.http.*

import java.io.ByteArrayInputStream
import java.util.Base64
import org.apache.poi.ss.usermodel.*

class ExelService extends Exel {
  def getProductFromExel(data: Array[Byte]): ZIO[Client & Scope, DomainError, List[ExelProduct]] = {
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
        ).map { row =>
        for {
          media <- fieldPositions.find(_._1 == ExelField.mediaFile).fold(None)((_, index) => Option(row.getCell(index)).map(_.getStringCellValue.split(";"))) match
            case None => ZIO.succeed(Array.empty[String])
            case Some(value) => loadImage(value)
        } yield ExelProduct(
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
    )
      .map(_.toList)
  }

  def loadImage(imageUrl: Array[String]): ZIO[Client & Scope, DomainError, Array[String]] =
    ZIO.collectAllPar(imageUrl.map(url =>
        for {
          response <- ZClient.request(Request.get(url))
          body <- response.body.asArray.map(Base64.getEncoder.encodeToString)
        } yield body
      )
    ).mapError(ex => LoadImageError(exception = ex))

}
object ExelService{
  val live = ZLayer.succeed(ExelService())
}