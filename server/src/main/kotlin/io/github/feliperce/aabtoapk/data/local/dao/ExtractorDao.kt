package io.github.feliperce.aabtoapk.data.local.dao

import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity

class ExtractorDao(
    private val extractorDb: ExtractorDb
) {

    fun insert(uploadedFilesDto: UploadedFilesDto): UploadedFilesEntity {
        return UploadedFilesEntity.new {
            name = uploadedFilesDto.name
            path = uploadedFilesDto.path
            uploadedDate = uploadedFilesDto.uploadedDate
        }
    }
}
