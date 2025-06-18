package feature.extractor.state

import feature.extractor.mapper.KeystoreDto
import feature.extractor.model.ExtractorFormData
import shared.settings.SettingsData
import io.github.feliperce.aabtoapk.utils.extractor.ErrorMsg
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsg
import ui.components.RadioItem

data class ExtractorUiState (
    val loading: Boolean = false,
    val errorMsg: ErrorMsg = ErrorMsg(),
    val successMsg: SuccessMsg = SuccessMsg(),
    val extractedApksPath: String = "",
    val settingsData: SettingsData? = null,
    val keystoreDtoList: List<KeystoreDto> = listOf(),
    val showKeystoreRemoveDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val aabPath: String = "",
    val keystoreDto: KeystoreDto = KeystoreDto(),
    val extractOptions: List<RadioItem> = listOf(),
    val selectedExtractOption: RadioItem? = null
)
