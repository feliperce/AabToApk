package io.github.feliperce.aabtoapk.feature.extractor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import io.github.feliperce.aabtoapk.feature.extractor.repository.ExtractorRepository
import io.github.feliperce.aabtoapk.feature.extractor.state.ExtractorIntent
import io.github.feliperce.aabtoapk.feature.extractor.state.ExtractorUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
        println("ENTROU VIEW MODEL UPLOAD")
        viewModelScope.launch {
            extractorRepository.uploadAndExtract(
                keystoreDto = keystoreDto,
                aabFileDto = aabFileDto
            )
        }
    }

}