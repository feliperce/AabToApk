package io.github.feliperce.aabtoapk.data.dto

import kotlinx.datetime.LocalDateTime

data class UploadedFilesDto(
    val id: Int,
    val name: String,
    val path: String,
    val uploadedDate: LocalDateTime
)
