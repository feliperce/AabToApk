package feature.extractor.state

import feature.extractor.model.ExtractorFormData
import shared.settings.SettingsData
import utils.ErrorMsg
import utils.SuccessMsg

data class ExtractorUiState (
    val loading: Boolean = false,
    val errorMsg: ErrorMsg = ErrorMsg(),
    val successMsg: SuccessMsg = SuccessMsg(),
    val extractedApksPath: String = "",
    val settingsData: SettingsData? = null
)

sealed class ExtractorIntent {
    class ExtractAab(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class InstallApks(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    data object GetSettingsData : ExtractorIntent()
}