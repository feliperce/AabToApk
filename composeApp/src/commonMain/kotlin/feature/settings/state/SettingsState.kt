package feature.settings.state

import feature.settings.model.SettingsFormData
import shared.settings.SettingsData
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsg

data class SettingsUiState (
    val settingsData: SettingsData? = null,
    val isFormValid: Boolean = false,
    val isFirstAccess: Boolean = true,
    val successMsg: SuccessMsg = SuccessMsg()
)

sealed class SettingsIntent {
    class SaveSettings(val settingsFormData: SettingsFormData) : SettingsIntent()
    class ValidateForm(val settingsFormData: SettingsFormData) : SettingsIntent()
    class SaveIsFirstAccess(val isFirstAccess: Boolean) : SettingsIntent()
    data object GetSettings : SettingsIntent()
    data object GetIsFirstAccess: SettingsIntent()
}