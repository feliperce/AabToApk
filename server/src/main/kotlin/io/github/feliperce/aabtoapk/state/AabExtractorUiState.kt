package io.github.feliperce.aabtoapk.state

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse

data class AabExtractorUiState(
    val loading: Boolean = false,
    val basePathDto: BasePathDto? = null,
    val extractedFilesDto: ExtractedFilesDto? = null,
    val errorResponse: ErrorResponse? = null
)