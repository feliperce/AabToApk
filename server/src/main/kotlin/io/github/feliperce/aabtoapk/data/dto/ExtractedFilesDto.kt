package io.github.feliperce.aabtoapk.data.dto

import kotlinx.datetime.Instant

data class ExtractedFilesDto(
    val id: Int = -1,
    val basePathDto: BasePathDto,
    val uploadedFilesDto: UploadedFilesDto,
    val name: String,
    val path: String,
    val fileExtension: String,
    val formattedName: String,
    val isDebugKeystore: Boolean,
    val extractedDate: Instant,
    val downloadUrl: String
)
