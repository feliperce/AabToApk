package io.github.feliperce.aabtoapk.data.dto

import kotlinx.datetime.LocalDateTime

data class ExtractedFilesDto(
    val id: Int = -1,
    val uploadedFileId: Int,
    val name: String,
    val path: String,
    val fileType: String,
    val isDebugKeystore: Boolean,
    val extractedDate: LocalDateTime,
    val downloadUrl: String
)
