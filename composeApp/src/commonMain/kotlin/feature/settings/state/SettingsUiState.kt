package feature.settings.state

import shared.settings.SettingsData
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsg

data class SettingsUiState (
    val settingsData: SettingsData? = null,
    val isFormValid: Boolean = false,
    val isFirstAccess: Boolean = true,
    val successMsg: SuccessMsg = SuccessMsg()
)