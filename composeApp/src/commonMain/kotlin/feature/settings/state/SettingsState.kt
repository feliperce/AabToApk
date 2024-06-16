package feature.settings.state

import feature.settings.model.SettingsFormData
import utils.ErrorMsg
import utils.SuccessMsg

data class SettingsUiState (
    val loading: Boolean = false,
    val errorMsg: ErrorMsg = ErrorMsg(),
    val successMsg: SuccessMsg = SuccessMsg(),
    val isFormValid: Boolean
)

sealed class SettingsIntent {
    class SaveSettings(val settingsFormData: SettingsFormData) : SettingsIntent()
    class ValidateForm(val settingsFormData: SettingsFormData) : SettingsIntent()
}