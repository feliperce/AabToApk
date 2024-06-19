package feature.extractor.mapper

data class KeystoreDto(
    val id: Long? = null,
    val name: String = "",
    val path: String = "",
    val password: String = "",
    val keyAlias: String = "",
    val keyPassword: String = ""
)
