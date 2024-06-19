package feature.extractor.mapper

data class KeystoreDto(
    val id: Int = -1,
    val name: String = "",
    val path: String = "",
    val password: String = "",
    val keyAlias: String = "",
    val keyPassword: String = ""
)
