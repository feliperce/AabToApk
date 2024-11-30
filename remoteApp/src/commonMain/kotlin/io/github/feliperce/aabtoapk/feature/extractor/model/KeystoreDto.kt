package io.github.feliperce.aabtoapk.feature.extractor.model

data class KeystoreDto(
    val isDebugKeystore: Boolean = true,
    val keystoreByteArray: ByteArray = byteArrayOf(),
    val keystoreFileName: String = "",
    val password: String = "",
    val alias: String = "",
    val keyPassword: String = ""
)
