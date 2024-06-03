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
                adbPath = extractorFormData.adbPath,
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
                onSuccess = {
                    _homeState.update {
                        it.copy(
                            loading = false,
                            successText = "Apks extracted with success!"
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