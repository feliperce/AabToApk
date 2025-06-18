package io.github.feliperce.aabtoapk.viewmodel

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.ExtractorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.state.AabExtractorIntent
import io.github.feliperce.aabtoapk.state.AabExtractorUiState
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AabExtractorViewModel(
    private val aabExtractorRepository: AabExtractorRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val intentChannel = Channel<AabExtractorIntent>(Channel.UNLIMITED)

    private val _aabExtractorState = MutableStateFlow(AabExtractorUiState())
    val aabExtractorState: StateFlow<AabExtractorUiState> = _aabExtractorState.asStateFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: AabExtractorIntent) {
        scope.launch {
            intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        intentChannel
            .consumeAsFlow()
            .onEach { intent ->
                when(intent) {
                    is AabExtractorIntent.Extract -> {
                        extract(
                            fileName = intent.fileName,
                            fileBytes = intent.fileBytes,
                            keystoreInfoDto = intent.keystoreInfoDto,
                            extractorOption = intent.extractorOption
                        )
                    }
                    is AabExtractorIntent.GetBasePathByName -> {
                        getBasePathByName(intent.name)
                    }
                    is AabExtractorIntent.GetExtractedByBasePath -> {
                        getExtractedByBasePath(intent.basePathDto)
                    }
                }
            }.launchIn(scope)
    }

    private fun reduce(currentState: AabExtractorUiState, newState: AabExtractorUiState): AabExtractorUiState {
        return newState
    }

    private fun updateState(stateReducer: (AabExtractorUiState) -> AabExtractorUiState) {
        _aabExtractorState.update { currentState ->
            reduce(currentState, stateReducer(currentState))
        }
    }

    suspend fun extract(
        fileName: String,
        fileBytes: ByteArray,
        keystoreInfoDto: KeystoreInfoDto?,
        extractorOption: ApksExtractor.ExtractorOption
    ): Flow<Resource<ExtractorResponse, ErrorResponse>> {
        updateState { it.copy(loading = true) }

        val basePathDto = aabExtractorRepository.insertBasePath()
        updateState { it.copy(basePathDto = basePathDto) }

        val keystore = keystoreInfoDto?.let {
            aabExtractorRepository.uploadKeystore(
                keystoreInfoDto = it,
                extractPath = basePathDto.path
            )
        }

        val uploadedFilesDto = aabExtractorRepository.uploadAab(
            fileName = fileName,
            basePathDto = basePathDto,
            fileBytes = fileBytes
        )

        val extractor = ApksExtractor(
            aabPath = uploadedFilesDto.path,
            outputApksPath = basePathDto.path,
            buildToolsPath = ServerConstants.PathConf.BUILD_TOOLS_PATH
        )

        return aabExtractorRepository.extract(
            uploadedFilesDto = uploadedFilesDto,
            extractor = extractor,
            keystoreInfoDto = keystore,
            extractorOption = extractorOption
        ).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    updateState { it.copy(loading = false) }
                }
                is Resource.Error -> {
                    updateState { it.copy(loading = false, errorResponse = resource.error) }
                }
                is Resource.Loading -> {
                    updateState { it.copy(loading = resource.isLoading) }
                }
            }
        }
    }

    suspend fun getBasePathByName(name: String): BasePathDto? {
        val basePathDto = aabExtractorRepository.getBasePathByName(name)
        updateState { it.copy(basePathDto = basePathDto) }
        return basePathDto
    }

    suspend fun getExtractedByBasePath(basePathDto: BasePathDto): ExtractedFilesDto? {
        val extractedFilesDto = aabExtractorRepository.getExtractedFileByBasePath(basePathDto)
        updateState { it.copy(extractedFilesDto = extractedFilesDto) }
        return extractedFilesDto
    }
}
