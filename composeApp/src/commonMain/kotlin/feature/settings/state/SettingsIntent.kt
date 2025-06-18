package feature.settings.state

import feature.settings.model.SettingsFormData

sealed class SettingsIntent {
    class SaveSettings(val settingsFormData: SettingsFormData) : SettingsIntent()
    class ValidateForm(val settingsFormData: SettingsFormData) : SettingsIntent()
    class SaveIsFirstAccess(val isFirstAccess: Boolean) : SettingsIntent()
    data object GetSettings : SettingsIntent()
    data object GetIsFirstAccess: SettingsIntent()
}