package shared.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath


fun createDataStore(
    producePath: () -> String,
): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    migrations = emptyList(),
    produceFile = { producePath().toPath() },
)

class AppSettings(private val dataStore: DataStore<Preferences>) {

    val settingsData: Flow<SettingsData> =
        dataStore.data.map { preferences ->
            SettingsData(
                adbPath = preferences[ADB_PATH_PREF_KEY] ?: "",
                buildToolsPath = preferences[BUILD_TOOLS_PATH_PREF_KEY] ?: "",
                outputPath = preferences[OUTPUT_PATH_PREF_KEY] ?: ""
            )
        }

    val isFirstAccess: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_FIRST_ACCESS_PREF_KEY] ?: true
        }

    suspend fun updateAdbPath(adbPath: String) {
        dataStore.edit { preferences ->
            preferences[ADB_PATH_PREF_KEY] = adbPath
        }
    }

    suspend fun updateBuildToolsPath(buildToolsPath: String) {
        dataStore.edit { preferences ->
            preferences[BUILD_TOOLS_PATH_PREF_KEY] = buildToolsPath
        }
    }

    suspend fun updateOutputPath(outputPath: String) {
        dataStore.edit { preferences ->
            preferences[OUTPUT_PATH_PREF_KEY] = outputPath
        }
    }

    suspend fun updateIsFirstAccess(isFirstAccess: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_ACCESS_PREF_KEY] = isFirstAccess
        }
    }

    suspend fun updateSettingsData(settingsData: SettingsData) {
        dataStore.edit { preferences ->
            preferences[ADB_PATH_PREF_KEY] = settingsData.adbPath
            preferences[BUILD_TOOLS_PATH_PREF_KEY] = settingsData.buildToolsPath
            preferences[OUTPUT_PATH_PREF_KEY] = settingsData.outputPath
        }
    }

    companion object {
        val ADB_PATH_PREF_KEY = stringPreferencesKey("adbPath")
        val BUILD_TOOLS_PATH_PREF_KEY = stringPreferencesKey("buildToolsPath")
        val OUTPUT_PATH_PREF_KEY = stringPreferencesKey("outputPath")
        val IS_FIRST_ACCESS_PREF_KEY = booleanPreferencesKey("isFirstAccess")

        const val DATA_STORE_FILE = "settings.preferences_pb"
    }
}