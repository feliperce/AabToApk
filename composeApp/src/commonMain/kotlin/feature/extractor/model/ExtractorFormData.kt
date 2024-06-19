package feature.extractor.model

import shared.settings.SettingsData

data class ExtractorFormData(
    val settingsData: SettingsData? = null,
    val aabPath: String = "",
    val isOverwriteApks: Boolean = false,
    val keystorePath: String = "",
    val keystorePassword: String = "",
    val keystoreAlias: String = "",
    val keyPassword: String = "",
)

data class ExtractorFormDataCallback(
    val onKeystorePathIconClick: () -> Unit,
    val onAabPathIconClick: () -> Unit
)