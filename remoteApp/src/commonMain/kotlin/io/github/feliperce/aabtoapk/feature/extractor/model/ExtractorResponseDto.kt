package io.github.feliperce.aabtoapk.feature.extractor.model

data class ExtractorResponseDto(
    val fileName: String,
    val fileType: String,
    val downloadUrl: String,
    val debugKeystore: Boolean
)
