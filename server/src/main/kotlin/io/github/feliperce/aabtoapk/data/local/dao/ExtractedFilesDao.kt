package io.github.feliperce.aabtoapk.data.local.dao

import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.entity.ExtractedFileEntity
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity
import io.github.feliperce.aabtoapk.data.mapper.toExtractedFilesDto
import org.jetbrains.exposed.sql.transactions.transaction

class ExtractedFilesDao {

    fun insert(
        extractedFilesDto: ExtractedFilesDto
    ): ExtractedFilesDto {
        return transaction {
            val uploadedFile = UploadedFilesEntity.findById(extractedFilesDto.uploadedFileId)
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
            }.toExtractedFilesDto(extractedFilesDto.uploadedFileId)
        }
    }

    fun getByHash(
        hash: String
    ): ExtractedFilesDto? {
        return transaction {
            val extractedFile = ExtractedFileEntity.find { ExtractorDb.ExtractedFiles.formattedName eq hash }.firstOrNull()

            extractedFile?.toExtractedFilesDto(extractedFile.aabFile.id.value)
        }
    }
}