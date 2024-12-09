package io.github.feliperce.aabtoapk.feature.extractor.mapper

import io.github.feliperce.aabtoapk.data.remote.response.ExtractorResponse
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorResponseDto

fun ExtractorResponse.toExtractorResponseDto() =
    ExtractorResponseDto(
        fileName = fileName,
        fileType = fileType,
        downloadUrl = downloadUrl,
        debugKeystore = debugKeystore
    )