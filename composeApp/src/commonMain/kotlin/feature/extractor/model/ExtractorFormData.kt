package feature.extractor.model

import feature.extractor.mapper.KeystoreDto
import shared.settings.SettingsData
import ui.components.RadioItem

data class ExtractorFormData(
    val settingsData: SettingsData? = null,
    val keystoreDto: KeystoreDto = KeystoreDto(),
    val aabPath: String = "",
    val extractOptions: List<RadioItem> = listOf(),
    val selectedExtractOption: RadioItem
)

data class ExtractorFormDataCallback(
    val onKeystoreSpinnerItemChanged: (KeystoreDto) -> Unit,
    val onKeystoreRemoveClick: () -> Unit,
    val onItemSelected: (radioItem: RadioItem) -> Unit
)