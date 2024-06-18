package feature.settings.repository

import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import shared.settings.AppSettings
import shared.settings.SettingsData

class SettingsRepository(
    private val appSettings: AppSettings
) {

    suspend fun getSettings() = flow {
        emitAll(appSettings.settingsData)
    }

    suspend fun getIsFirstAccess() = flow {
        emitAll(appSettings.isFirstAccess)
    }

    suspend fun saveSettings(settingsData: SettingsData) {
        appSettings.updateSettingsData(settingsData)
    }

    suspend fun saveIsFirstAccess(isFirstAccess: Boolean) {
        appSettings.updateIsFirstAccess(isFirstAccess)
    }
}