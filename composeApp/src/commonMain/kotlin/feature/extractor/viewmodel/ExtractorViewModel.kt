package feature.extractor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feature.extractor.mapper.KeystoreDto
import feature.extractor.model.ExtractorFormData
import feature.extractor.repository.ExtractorRepository
import feature.extractor.state.ExtractorIntent
import feature.extractor.state.ExtractorUiState
import feature.settings.repository.SettingsRepository
import io.github.feliperce.aabtoapk.utils.extractor.*
import ui.components.RadioItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class ExtractorViewModel(
    private val extractorRepository: ExtractorRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val intentChannel = Channel<ExtractorIntent>(Channel.UNLIMITED)
    private var apksExtractor: ApksExtractor? = null

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
                    is ExtractorIntent.InstallApk -> {
                        installApk(intent.extractorFormData)
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
                    is ExtractorIntent.SetShowKeystoreRemoveDialog -> {
                        setShowKeystoreRemoveDialog(intent.show)
                    }
                    is ExtractorIntent.SetShowErrorDialog -> {
                        setShowErrorDialog(intent.show)
                    }
                    is ExtractorIntent.UpdateAabPath -> {
                        updateAabPath(intent.path)
                    }
                    is ExtractorIntent.UpdateKeystoreDto -> {
                        updateKeystoreDto(intent.keystoreDto)
                    }
                    is ExtractorIntent.UpdateExtractOptions -> {
                        updateExtractOptions(intent.options)
                    }
                    is ExtractorIntent.UpdateSelectedExtractOption -> {
                        updateSelectedExtractOption(intent.option)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun reduce(currentState: ExtractorUiState, newState: ExtractorUiState): ExtractorUiState {
        return newState
    }

    private fun updateState(stateReducer: (ExtractorUiState) -> ExtractorUiState) {
        _extractorState.update { currentState ->
            reduce(currentState, stateReducer(currentState))
        }
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
                updateState { it.copy(keystoreDtoList = keystoreList) }
            }
        }
    }

    private fun getSettingsData() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                updateState { it.copy(settingsData = settings) }
            }
        }
    }

    private fun extractAab(extractorFormData: ExtractorFormData) {
        updateState { it.copy(loading = true) }

        extractorFormData.settingsData?.let { settingsData ->
            viewModelScope.launch {
                val keystoreDto = extractorFormData.keystoreDto

                apksExtractor = ApksExtractor(
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
                            updateState { it.copy(loading = false, errorMsg = errorMsg) }
                        }
                    )
                }

                apksExtractor?.aabToApks(
                    apksFileName = File(extractorFormData.aabPath).nameWithoutExtension,
                    extractorOption = extractorFormData.selectedExtractOption.data as ApksExtractor.ExtractorOption,
                    onSuccess = { output, _ ->
                        updateState { 
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
                        updateState { it.copy(loading = false, errorMsg = errorMsg) }
                    }
                )
            }
        } ?: run {
            updateState { 
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
        updateState { it.copy(loading = true) }

        extractorFormData.settingsData?.let { settingsData ->
            val apksInstall = ApksInstall(settingsData.adbPath)

            viewModelScope.launch {
                apksInstall.installApks(
                    apksPath = _extractorState.value.extractedApksPath,
                    onSuccess = {
                        updateState { 
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
                        updateState { it.copy(loading = false, errorMsg = errorMsg) }
                    }
                )
            }
        } ?: run {
            updateState { 
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

    private fun installApk(extractorFormData: ExtractorFormData) {
        updateState { it.copy(loading = true) }

        extractorFormData.settingsData?.let { settingsData ->
            val apkInstall = ApkInstall(settingsData.adbPath)

            viewModelScope.launch {
                apkInstall.installApk(
                    apkPath = _extractorState.value.extractedApksPath,
                    onSuccess = {
                        updateState { 
                            it.copy(
                                loading = false,
                                successMsg = SuccessMsg(
                                    msg = "APK installed with success!",
                                    type = SuccessMsgType.INSTALL_APKS
                                )
                            )
                        }
                    },
                    onFailure = { errorMsg ->
                        updateState { it.copy(loading = false, errorMsg = errorMsg) }
                    }
                )
            }
        } ?: run {
            updateState { 
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
    private fun setShowKeystoreRemoveDialog(show: Boolean) {
        updateState { it.copy(showKeystoreRemoveDialog = show) }
    }

    private fun setShowErrorDialog(show: Boolean) {
        updateState { it.copy(showErrorDialog = show) }
    }

    private fun updateAabPath(path: String) {
        updateState { it.copy(aabPath = path) }
    }

    private fun updateKeystoreDto(keystoreDto: KeystoreDto) {
        updateState { it.copy(keystoreDto = keystoreDto) }
    }

    private fun updateExtractOptions(options: List<RadioItem>) {
        updateState { it.copy(extractOptions = options) }
    }

    private fun updateSelectedExtractOption(option: RadioItem) {
        updateState { it.copy(selectedExtractOption = option) }
    }
}
