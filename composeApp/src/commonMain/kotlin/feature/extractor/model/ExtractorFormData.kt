package feature.extractor.model

import feature.extractor.mapper.KeystoreDto
import shared.settings.SettingsData

data class ExtractorFormData(
    val settingsData: SettingsData? = null,
    val keystoreDto: KeystoreDto = KeystoreDto(),
    val aabPath: String = "",
    val isOverwriteApks: Boolean = false
)

data class ExtractorFormDataCallback(
    val onKeystorePathIconClick: () -> Unit,
    val onAabPathIconClick: () -> Unit,
    val onItemChanged: (KeystoreDto) -> Unit
)