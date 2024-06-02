package feature.home.model

data class ExtractorFormData(
    val aabPath: String = "",
    val outputApksPath: String = "",
    val isOverwriteApks: Boolean = false,
    val keystorePath: String = "",
    val keystorePassword: String = "",
    val keystoreAlias: String = "",
    val keyPassword: String = "",
    val adbPath: String = ""
)

data class ExtractorFormDataCallback(
    val onAdbPathIconClick: () -> Unit,
    val onKeystorePathIconClick: () -> Unit,
    val onAabPathIconClick: () -> Unit,
    val onOutputPathIconClick: () -> Unit
)