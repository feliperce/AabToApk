package io.github.feliperce.aabtoapk.feature.extractor.state

import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorResponseDto
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import ui.handler.DefaultErrorMsg

data class ExtractorUiState (
    val loading: Boolean = false,
    val extractorResponseDto: ExtractorResponseDto? = null,
    val aabFileDto: AabFileDto = AabFileDto(),
    val keystore: KeystoreDto = KeystoreDto(),
    val errorMsg: DefaultErrorMsg? = null
)