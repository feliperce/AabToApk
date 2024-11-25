package io.github.feliperce.aabtoapk.data.local.dao

import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity
import io.github.feliperce.aabtoapk.data.mapper.toUploadFilesDto
import org.jetbrains.exposed.sql.transactions.transaction

class UploadFilesDao {

    fun insert(uploadedFilesDto: UploadedFilesDto): UploadedFilesDto {
        return transaction {
            UploadedFilesEntity.new {
                name = uploadedFilesDto.name
                path = uploadedFilesDto.path
                formattedName = uploadedFilesDto.formattedName
                uploadedDate = uploadedFilesDto.uploadedDate
                hash = uploadedFilesDto.hash
            }
        }.toUploadFilesDto()
    }
}
