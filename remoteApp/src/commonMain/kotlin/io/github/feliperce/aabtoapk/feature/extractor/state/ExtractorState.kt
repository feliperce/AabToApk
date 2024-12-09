package io.github.feliperce.aabtoapk.feature.extractor.state

import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorResponseDto
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import ui.handler.DefaultErrorMsg
import ui.handler.DefaultSuccessMsg

data class ExtractorUiState (
    val loading: Boolean = false,
    var extractorResponseDto: ExtractorResponseDto? = null,
    var aabFileDto: AabFileDto = AabFileDto(),
    var keystore: KeystoreDto = KeystoreDto(),
    val errorMsg: DefaultErrorMsg? = null
)

sealed class ExtractorIntent {
    class UploadAndExtract(val keystoreDto: KeystoreDto, val aabFileDto: AabFileDto) : ExtractorIntent()
}
