package io.github.feliperce.aabtoapk.data.local.dao

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.entity.BasePathEntity
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity
import io.github.feliperce.aabtoapk.data.mapper.toUploadFilesDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class UploadFilesDao {

    fun insert(uploadedFilesDto: UploadedFilesDto): UploadedFilesDto {
        return transaction {
            val basePath = BasePathEntity.findById(uploadedFilesDto.basePathDto.id)
                ?: throw IllegalArgumentException("db base path file not found")

            UploadedFilesEntity.new {
                name = uploadedFilesDto.name
                path = uploadedFilesDto.path
                formattedName = uploadedFilesDto.formattedName
                uploadedDate = uploadedFilesDto.uploadedDate
                this.basePath = basePath
            }.toUploadFilesDto()
        }
    }

    fun getByBasePath(
        basePathDto: BasePathDto
    ): UploadedFilesDto? {
        return transaction {
            val basePath = BasePathEntity.findById(basePathDto.id)
                ?: throw IllegalArgumentException("db BasePath not found")

            val uploadedFile =
                UploadedFilesEntity.find { ExtractorDb.UploadedFiles.basePath eq basePath.id }.firstOrNull()

            uploadedFile?.toUploadFilesDto()
        }
    }

    fun removeByBasePathId(
        basePathId: Int
    ): Int {
        return transaction {
            ExtractorDb.UploadedFiles.deleteWhere { basePath eq basePathId }
        }
    }
}
