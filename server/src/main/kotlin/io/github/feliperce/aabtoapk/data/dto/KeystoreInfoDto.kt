package io.github.feliperce.aabtoapk.data.dto

data class KeystoreInfoDto(
    val name: String,
    val fileBytes: ByteArray,
    val fileExtension: String,
    val path: String = "",
    val password: String,
    val keyAlias: String,
    val keyPassword: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeystoreInfoDto

        return fileBytes.contentEquals(other.fileBytes)
    }

    override fun hashCode(): Int {
        return fileBytes.contentHashCode()
    }
}
