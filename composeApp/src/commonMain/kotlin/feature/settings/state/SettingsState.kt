package feature.settings.state

import feature.settings.model.SettingsFormData
import shared.settings.SettingsData
import utils.ErrorMsg
import utils.SuccessMsg

data class SettingsUiState (
    val settingsData: SettingsData? = null,
    val isFormValid: Boolean
)

sealed class SettingsIntent {
    class SaveSettings(val settingsFormData: SettingsFormData) : SettingsIntent()
    class ValidateForm(val settingsFormData: SettingsFormData) : SettingsIntent()
    data object GetSettings : SettingsIntent()
}