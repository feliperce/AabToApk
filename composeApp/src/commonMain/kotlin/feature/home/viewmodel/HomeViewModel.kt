package feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feature.home.model.ExtractorFormData
import feature.home.state.HomeIntent
import feature.home.state.HomeUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import utils.ApkExtractor
import utils.ApkInstall
import utils.SuccessMsg
import utils.SuccessMsgType
import java.io.File

class HomeViewModel : ViewModel() {

    private val intentChannel = Channel<HomeIntent>(Channel.UNLIMITED)
    private var apkExtractor: ApkExtractor? = null

    private val _homeState = MutableStateFlow(HomeUiState(loading = false))
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: HomeIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        intentChannel
            .consumeAsFlow()
            .onEach { intent ->
                when(intent) {
                    is HomeIntent.ExtractAab -> {
                        extractAab(intent.extractorFormData)
                    }
                    is HomeIntent.InstallApks -> {
                        installApks(intent.extractorFormData)
                    }
                }
            }.launchIn(viewModelScope)
    }


    private fun extractAab(extractorFormData: ExtractorFormData) {
        _homeState.update {
            it.copy(
                loading = true
            )
        }

        viewModelScope.launch {
            apkExtractor = ApkExtractor(
                aabPath = extractorFormData.aabPath,
                outputApksPath = extractorFormData.outputApksPath
            ).apply {
                setSignConfig(
                    keystorePath = extractorFormData.keystorePath,
                    keyAlias = extractorFormData.keystoreAlias,
                    keystorePassword = extractorFormData.keystorePassword,
                    keyPassword = extractorFormData.keyPassword,
                    onFailure = { errorMsg ->
                        _homeState.update {
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
                    _homeState.update {
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
                    _homeState.update {
                        it.copy(
                            loading = false,
                            errorMsg = errorMsg
                        )
                    }
                }
            )
        }
    }

    private fun installApks(extractorFormData: ExtractorFormData) {
        _homeState.update {
            it.copy(
                loading = true
            )
        }

        val apkInstall = ApkInstall(extractorFormData.adbPath)

        viewModelScope.launch {
            apkInstall.installApks(
                apksPath = _homeState.value.extractedApksPath,
                onSuccess = {
                    _homeState.update {
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
                    _homeState.update {
                        it.copy(
                            loading = false,
                            errorMsg = errorMsg
                        )
                    }
                }
            )
        }
    }

}