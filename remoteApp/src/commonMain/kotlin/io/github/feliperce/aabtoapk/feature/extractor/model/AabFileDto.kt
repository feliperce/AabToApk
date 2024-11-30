package io.github.feliperce.aabtoapk.feature.extractor.model

data class AabFileDto(
    val aabByteArray: ByteArray = byteArrayOf(),
    val fileName: String = ""
)
