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


}