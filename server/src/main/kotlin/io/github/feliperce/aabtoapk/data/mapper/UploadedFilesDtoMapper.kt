package io.github.feliperce.aabtoapk.data.mapper

import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.entity.ExtractedFileEntity
import io.github.feliperce.aabtoapk.data.local.entity.UploadedFilesEntity

fun UploadedFilesEntity.toUploadFilesDto() =
    UploadedFilesDto(
        id = id.value,
        basePathDto = basePath.toBasePathDto(),
        name = name,
        path = path,
        uploadedDate = uploadedDate,
        formattedName = formattedName
    )

fun Iterable<UploadedFilesEntity>.toUploadFilesDtoList() = map { it.toUploadFilesDto() }

fun ExtractedFileEntity.toExtractedFilesDto() =
    ExtractedFilesDto(
        id = id.value,
        uploadedFilesDto = aabFile.toUploadFilesDto(),
        basePathDto = basePath.toBasePathDto(),
        name = name,
        path = path,
        fileExtension = fileType,
        isDebugKeystore = isDebugKeystore,
        extractedDate = extractedDate,
        downloadUrl = downloadUrl,
        formattedName = formattedName
    )

fun Iterable<ExtractedFileEntity>.toExtractedFilesDtoList() = map { it.toExtractedFilesDto() }