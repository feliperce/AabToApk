package io.github.feliperce.aabtoapk.data.local.dao

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.entity.BasePathEntity
import io.github.feliperce.aabtoapk.data.local.entity.ExtractedFileEntity
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity
import io.github.feliperce.aabtoapk.data.mapper.toExtractedFilesDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class ExtractedFilesDao {

    fun insert(
        extractedFilesDto: ExtractedFilesDto
    ): ExtractedFilesDto {
        return transaction {
            val basePath = BasePathEntity.findById(extractedFilesDto.basePathDto.id)
                ?: throw IllegalArgumentException("db BasePath not found")

            val uploadedFile = UploadedFilesEntity.findById(extractedFilesDto.uploadedFilesDto.id)
                ?: throw IllegalArgumentException("db Uploaded file not found")

            ExtractedFileEntity.new {
                fileType = extractedFilesDto.fileExtension
                downloadUrl = extractedFilesDto.downloadUrl
                isDebugKeystore = extractedFilesDto.isDebugKeystore
                name = extractedFilesDto.name
                path = extractedFilesDto.path
                extractedDate = extractedFilesDto.extractedDate
                formattedName = extractedFilesDto.formattedName
                aabFile = uploadedFile
                this.basePath = basePath
            }.toExtractedFilesDto()
        }
    }

    fun getByBasePath(
        basePathDto: BasePathDto
    ): ExtractedFilesDto? {
        return transaction {
            val basePath = BasePathEntity.findById(basePathDto.id)
                ?: throw IllegalArgumentException("db BasePath not found")

            val extractedFile = ExtractedFileEntity.find { ExtractorDb.ExtractedFiles.basePath eq basePath.id }.firstOrNull()

            extractedFile?.delete()

            extractedFile?.toExtractedFilesDto()
        }
    }

    fun removeByBasePathId(
        basePathId: Int
    ): Int {
        return transaction {
            ExtractorDb.ExtractedFiles.deleteWhere { basePath eq basePathId }
        }
    }
}