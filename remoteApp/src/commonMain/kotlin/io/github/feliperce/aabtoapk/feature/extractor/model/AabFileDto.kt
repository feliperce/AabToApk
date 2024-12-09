package io.github.feliperce.aabtoapk.feature.extractor.model

data class AabFileDto(
    val aabByteArray: ByteArray = byteArrayOf(),
    val fileName: String = "",
    val fileSize: Long = 0,
    val extractorOption: ExtractorOption = ExtractorOption.APKS
)
