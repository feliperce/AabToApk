package feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feature.settings.model.SettingsFormData
import feature.settings.repository.SettingsRepository
import feature.settings.state.SettingsIntent
import feature.settings.state.SettingsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import shared.settings.SettingsData

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val intentChannel = Channel<SettingsIntent>(Channel.UNLIMITED)

    private val _settingsState = MutableStateFlow(SettingsUiState(isFormValid = false))
    val settingsState: StateFlow<SettingsUiState> = _settingsState.asStateFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: SettingsIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        intentChannel
            .consumeAsFlow()
            .onEach { intent ->
                when(intent) {
                    is SettingsIntent.GetSettings -> {
                        getSettings()
                    }
                    is SettingsIntent.SaveSettings -> {
                        saveSettings(intent.settingsFormData)
                    }
                    is SettingsIntent.ValidateForm -> {
                        validateForm(intent.settingsFormData)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settingsData ->
                _settingsState.update {
                    it.copy(settingsData = settingsData)
                }
            }
        }
    }

    private fun saveSettings(settingsFormData: SettingsFormData) {
        val settingsData = SettingsData(
            adbPath = settingsFormData.adbPath,
            buildToolsPath = settingsFormData.buildToolsPath,
            outputPath = settingsFormData.outputApksPath
        )

        viewModelScope.launch {
            settingsRepository.saveSettings(settingsData)
        }
    }

    private fun validateForm(settingsFormData: SettingsFormData) {
        _settingsState.update {
            it.copy(
                isFormValid = settingsFormData.adbPath.isNotEmpty() &&
                        settingsFormData.buildToolsPath.isNotEmpty() &&
                        settingsFormData.outputApksPath.isNotEmpty()
            )
        }
    }
}