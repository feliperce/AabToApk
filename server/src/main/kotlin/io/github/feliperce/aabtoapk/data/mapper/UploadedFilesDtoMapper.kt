package io.github.feliperce.aabtoapk.data.mapper

import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.entity.ExtractedFileEntity
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity

fun UploadedFilesEntity.toUploadFilesDto() =
    UploadedFilesDto(
        id = id.value,
        name = name,
        path = path,
        uploadedDate = uploadedDate
    )

fun ExtractedFileEntity.toExtractedFilesDto(uploadedFileId: Int) =
    ExtractedFilesDto(
        id = id.value,
        uploadedFileId = uploadedFileId,
        name = name,
        path = path,
        fileType = fileType,
        isDebugKeystore = isDebugKeystore,
        extractedDate = extractedDate,
        downloadUrl = downloadUrl
    )