package io.github.feliperce.aabtoapk.data.dto

import kotlinx.datetime.Instant

data class UploadedFilesDto(
    val id: Int = -1,
    val basePathDto: BasePathDto,
    val name: String,
    val path: String,
    val formattedName: String,
    val uploadedDate: Instant
)
