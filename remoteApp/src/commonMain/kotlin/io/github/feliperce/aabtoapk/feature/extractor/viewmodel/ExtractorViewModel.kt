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
                    is ExtractorIntent.UpdateKeystoreDebug -> {
                        updateState { 
                            it.copy(
                                keystore = it.keystore.copy(
                                    isDebugKeystore = intent.isDebugKeystore
                                )
                            ) 
                        }
                    }
                    is ExtractorIntent.UpdateExtractorOption -> {
                        updateState { 
                            it.copy(
                                aabFileDto = it.aabFileDto.copy(
                                    extractorOption = intent.extractorOption
                                )
                            ) 
                        }
                    }
                    is ExtractorIntent.UpdateAabFile -> {
                        viewModelScope.launch {
                            val file = intent.file
                            val fileBytes = file.readBytes()
                            val fileName = file.name
                            val fileSize = file.getSize() ?: 0
                            updateState { 
                                it.copy(
                                    aabFileDto = it.aabFileDto.copy(
                                        aabByteArray = fileBytes,
                                        fileName = fileName,
                                        fileSize = fileSize
                                    )
                                ) 
                            }
                        }
                    }
                    is ExtractorIntent.UpdateKeystoreFile -> {
                        viewModelScope.launch {
                            val file = intent.file
                            val fileName = file.name
                            val keystoreBytes = file.readBytes()
                            updateState { 
                                it.copy(
                                    keystore = it.keystore.copy(
                                        keystoreFileName = fileName,
                                        keystoreByteArray = keystoreBytes
                                    )
                                ) 
                            }
                        }
                    }
                    is ExtractorIntent.UpdateKeystorePassword -> {
                        updateState { 
                            it.copy(
                                keystore = it.keystore.copy(
                                    password = intent.password
                                )
                            ) 
                        }
                    }
                    is ExtractorIntent.UpdateKeystoreAlias -> {
                        updateState { 
                            it.copy(
                                keystore = it.keystore.copy(
                                    alias = intent.alias
                                )
                            ) 
                        }
                    }
                    is ExtractorIntent.UpdateKeystoreKeyPassword -> {
                        updateState { 
                            it.copy(
                                keystore = it.keystore.copy(
                                    keyPassword = intent.keyPassword
                                )
                            ) 
                        }
                    }
                    is ExtractorIntent.ResetExtractorResponse -> {
                        updateState { it.copy(extractorResponseDto = null) }
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
                                updateState { it.copy(extractorResponseDto = res.data) }
                            }
                            is Resource.Error -> {
                                updateState { it.copy(errorMsg = res.error ?: DefaultErrorMsg(msg = "GENERIC")) }
                            }
                            is Resource.Loading -> {
                                updateState { it.copy(loading = res.isLoading) }
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
                        updateState { it.copy(errorMsg = DefaultErrorMsg(
                            msg = "Enter the keystore password"
                        )) }
                        false
                    }
                    alias.isEmpty() -> {
                        updateState { it.copy(errorMsg = DefaultErrorMsg(
                            msg = "Enter the keystore key alias"
                        )) }
                        false
                    }
                    keyPassword.isEmpty() -> {
                        updateState { it.copy(errorMsg = DefaultErrorMsg(
                            msg = "Enter the keystore key password"
                        )) }
                        false
                    }
                    keystoreByteArray.isEmpty() -> {
                        updateState { it.copy(errorMsg = DefaultErrorMsg(
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
                updateState { it.copy(errorMsg = DefaultErrorMsg(
                    msg = "Enter the aab file"
                )) }
                false
            } else {
                if (this.fileSize > ServerConstants.MAX_AAB_UPLOAD_MB.convertMegaByteToBytesLong()) {
                    updateState { it.copy(errorMsg = DefaultErrorMsg(
                        msg = "Max file aab file size is: ${ServerConstants.MAX_AAB_UPLOAD_MB} mb"
                    )) }
                    return false
                }
                true
            }
        }
    }
}
