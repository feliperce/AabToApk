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
                    is ExtractorIntent.GetKeystoreData -> {
                        getKeystoreData()
                    }
                    is ExtractorIntent.RemoveKeystore -> {
                        removeKeystore(intent.keystoreDto)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun removeKeystore(keystoreDto: KeystoreDto) {
        viewModelScope.launch {
            extractorRepository.deleteKeystore(keystoreDto)
        }
    }

    private fun saveKeystore(keystoreDto: KeystoreDto) {
        viewModelScope.launch {
            extractorRepository.insertOrUpdateKeystore(keystoreDto)
        }
    }

    private fun getKeystoreData() {
        viewModelScope.launch {
            extractorRepository.getKeystoreAll().collect { keystoreList ->
                _extractorState.update {
                    it.copy(keystoreDtoList = keystoreList)
                }
            }
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
                val keystoreDto = extractorFormData.keystoreDto

                apkExtractor = ApkExtractor(
                    aabPath = extractorFormData.aabPath,
                    outputApksPath = settingsData.outputPath,
                    buildToolsPath = settingsData.buildToolsPath
                ).apply {
                    setSignConfig(
                        keystorePath = keystoreDto.path,
                        keyAlias = keystoreDto.keyAlias,
                        keystorePassword = keystoreDto.password,
                        keyPassword = keystoreDto.keyPassword,
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
                    extractorOption = extractorFormData.selectedExtractOption.data as ApkExtractor.ExtractorOption,
                    onSuccess = { output ->
                        _extractorState.update {
                            it.copy(
                                loading = false,
                                successMsg = SuccessMsg(
                                    msg = "Extracted with success: $output",
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