package feature.extractor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feature.extractor.mapper.KeystoreDto
import feature.extractor.model.ExtractorFormData
import feature.extractor.repository.ExtractorRepository
import feature.extractor.state.ExtractorIntent
import feature.extractor.state.ExtractorUiState
import feature.settings.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import utils.*
import java.io.File

class ExtractorViewModel(
    private val extractorRepository: ExtractorRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val intentChannel = Channel<ExtractorIntent>(Channel.UNLIMITED)
    private var apkExtractor: ApkExtractor? = null

    private val _extractorState = MutableStateFlow(ExtractorUiState(loading = false))
    val extractorState: StateFlow<ExtractorUiState> = _extractorState.asStateFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: ExtractorIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        intentChannel
            .consumeAsFlow()
            .onEach { intent ->
                when(intent) {
                    is ExtractorIntent.ExtractAab -> {
                        extractAab(intent.extractorFormData)
                    }
                    is ExtractorIntent.InstallApks -> {
                        installApks(intent.extractorFormData)
                    }
                    is ExtractorIntent.GetSettingsData -> {
                        getSettingsData()
                    }
                    is ExtractorIntent.SaveKeystore -> {
                        saveKeystore(intent.keystoreDto)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun saveKeystore(keystoreDto: KeystoreDto) {
        viewModelScope.launch {
            extractorRepository.insertOrUpdateKeystore(keystoreDto)
        }
    }

    private fun getSettingsData() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _extractorState.update {
                    it.copy(settingsData = settings)
                }
            }
        }
    }

    private fun extractAab(extractorFormData: ExtractorFormData) {
        _extractorState.update {
            it.copy(
                loading = true
            )
        }

        extractorFormData.settingsData?.let { settingsData ->
            viewModelScope.launch {
                apkExtractor = ApkExtractor(
                    aabPath = extractorFormData.aabPath,
                    outputApksPath = settingsData.outputPath,
                    buildToolsPath = settingsData.buildToolsPath
                ).apply {
                    setSignConfig(
                        keystorePath = extractorFormData.keystorePath,
                        keyAlias = extractorFormData.keystoreAlias,
                        keystorePassword = extractorFormData.keystorePassword,
                        keyPassword = extractorFormData.keyPassword,
                        onFailure = { errorMsg ->
                            _extractorState.update {
                                it.copy(
                                    loading = false,
                                    errorMsg = errorMsg
                                )
                            }
                        }
                    )
                }

                apkExtractor?.aabToApks(
                    apksFileName = File(extractorFormData.aabPath).nameWithoutExtension,
                    overwriteApks = extractorFormData.isOverwriteApks,
                    onSuccess = { output ->
                        _extractorState.update {
                            it.copy(
                                loading = false,
                                successMsg = SuccessMsg(
                                    msg = "Apks extracted with success: $output",
                                    type = SuccessMsgType.EXTRACT_AAB
                                ),
                                extractedApksPath = output
                            )
                        }
                    },
                    onFailure = { errorMsg ->
                        _extractorState.update {
                            it.copy(
                                loading = false,
                                errorMsg = errorMsg
                            )
                        }
                    }
                )
            }
        } ?: run {
            _extractorState.update {
                it.copy(
                    loading = false,
                    errorMsg = ErrorMsg(
                        title = "INVALID SETTINGS",
                        msg = "Go to the settings menu and check"
                    )
                )
            }
        }
    }

    private fun installApks(extractorFormData: ExtractorFormData) {
        _extractorState.update {
            it.copy(
                loading = true
            )
        }

        extractorFormData.settingsData?.let { settingsData ->
            val apkInstall = ApkInstall(settingsData.adbPath)

            viewModelScope.launch {
                apkInstall.installApks(
                    apksPath = _extractorState.value.extractedApksPath,
                    onSuccess = {
                        _extractorState.update {
                            it.copy(
                                loading = false,
                                successMsg = SuccessMsg(
                                    msg = "Apks installed with success!",
                                    type = SuccessMsgType.INSTALL_APKS
                                )
                            )
                        }
                    },
                    onFailure = { errorMsg ->
                        _extractorState.update {
                            it.copy(
                                loading = false,
                                errorMsg = errorMsg
                            )
                        }
                    }
                )
            }
        } ?: run {
            _extractorState.update {
                it.copy(
                    loading = false,
                    errorMsg = ErrorMsg(
                        title = "INVALID SETTINGS",
                        msg = "Go to the settings menu and check"
                    )
                )
            }
        }
    }
}