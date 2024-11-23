package io.github.feliperce.aabtoapk.data.mapper

import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity

fun UploadedFilesEntity.toUploadFilesMapper() =
    UploadedFilesDto(
        id = id.value,
        name = name,
        path = path,
        uploadedDate = uploadedDate
    )