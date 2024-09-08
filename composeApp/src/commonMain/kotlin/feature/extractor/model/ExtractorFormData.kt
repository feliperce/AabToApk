package feature.extractor.model

import feature.extractor.mapper.KeystoreDto
import shared.settings.SettingsData
import ui.components.RadioItem
import utils.ApkExtractor

data class ExtractorFormData(
    val settingsData: SettingsData? = null,
    val keystoreDto: KeystoreDto = KeystoreDto(),
    val aabPath: String = "",
    val extractorOption: ApkExtractor.ExtractorOption = ApkExtractor.ExtractorOption.APKS,
    val extractOptions: List<RadioItem> = listOf(),
)

data class ExtractorFormDataCallback(
    val onKeystorePathIconClick: () -> Unit,
    val onAabPathIconClick: () -> Unit,
    val onKeystoreSpinnerItemChanged: (KeystoreDto) -> Unit,
    val onKeystoreRemoveClick: () -> Unit,
    val onItemSelected: (radioItem: RadioItem) -> Unit
)