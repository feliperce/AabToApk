package io.github.feliperce.aabtoapk.feature.extractor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import io.github.feliperce.aabtoapk.feature.extractor.repository.ExtractorRepository
import io.github.feliperce.aabtoapk.feature.extractor.state.ExtractorIntent
import io.github.feliperce.aabtoapk.feature.extractor.state.ExtractorUiState
import io.github.feliperce.aabtoapk.utils.format.convertMegaByteToBytesLong
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ui.handler.DefaultErrorMsg

class ExtractorViewModel(
    private val extractorRepository: ExtractorRepository
) : ViewModel() {

    private val intentChannel = Channel<ExtractorIntent>(Channel.UNLIMITED)

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
                    is ExtractorIntent.UploadAndExtract -> {
                        uploadAndExtract(
                            keystoreDto = intent.keystoreDto,
                            aabFileDto = intent.aabFileDto
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun uploadAndExtract(
        keystoreDto: KeystoreDto,
        aabFileDto: AabFileDto
    ) {
        viewModelScope.launch {
            if (validateAabForm(aabFileDto)) {
                if (validateKeystoreForm(keystoreDto)) {
                    extractorRepository.uploadAndExtract(
                        keystoreDto = keystoreDto,
                        aabFileDto = aabFileDto
                    ).collect { res ->
                        when (res) {
                            is Resource.Success -> {
                                _extractorState.update {
                                    it.copy(extractorResponseDto = res.data)
                                }
                            }
                            is Resource.Error -> {
                                _extractorState.update {
                                    it.copy(errorMsg = res.error ?: DefaultErrorMsg(msg = "GENERIC"))
                                }
                            }
                            is Resource.Loading -> {
                                _extractorState.update {
                                    it.copy(loading = res.isLoading)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateKeystoreForm(keystoreDto: KeystoreDto): Boolean {
        return with(keystoreDto) {
            if (isDebugKeystore) {
                true
            } else {
                when {
                    password.isEmpty() -> {
                        _extractorState.update { it.copy(errorMsg = DefaultErrorMsg(
                            msg = "Enter the keystore password"
                        )) }
                        false
                    }
                    alias.isEmpty() -> {
                        _extractorState.update { it.copy(errorMsg = DefaultErrorMsg(
                            msg = "Enter the keystore key alias"
                        )) }
                        false
                    }
                    keyPassword.isEmpty() -> {
                        _extractorState.update { it.copy(errorMsg = DefaultErrorMsg(
                            msg = "Enter the keystore key password"
                        )) }
                        false
                    }
                    keystoreByteArray.isEmpty() -> {
                        _extractorState.update { it.copy(errorMsg = DefaultErrorMsg(
                            msg ="Enter the keystore file"
                        )) }
                        false
                    }
                    else -> true
                }
            }
        }
    }

    private fun validateAabForm(aabFileDto: AabFileDto): Boolean {
        return with(aabFileDto) {
            if (aabByteArray.isEmpty() || this.fileName.isEmpty()) {
                _extractorState.update { it.copy(errorMsg = DefaultErrorMsg(
                    msg = "Enter the aab file"
                )) }
                false
            } else {
                if (this.fileSize > ServerConstants.MAX_AAB_UPLOAD_MB.convertMegaByteToBytesLong()) {
                    _extractorState.update { it.copy(errorMsg = DefaultErrorMsg(
                        msg = "Max file aab file size is: ${ServerConstants.MAX_AAB_UPLOAD_MB} mb"
                    )) }
                    return false
                }
                true
            }
        }
    }

}