package io.github.feliperce.aabtoapk.data.dto

import kotlinx.datetime.LocalDateTime

data class UploadedFilesDto(
    val id: Int = -1,
    val name: String,
    val path: String,
    val formattedName: String,
    val uploadedDate: LocalDateTime
)
