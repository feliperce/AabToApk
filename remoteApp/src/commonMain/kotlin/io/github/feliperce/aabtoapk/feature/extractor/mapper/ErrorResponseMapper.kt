package io.github.feliperce.aabtoapk.feature.extractor.mapper

import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import ui.handler.DefaultErrorMsg

fun ErrorResponse.toErrorMsg() =
    DefaultErrorMsg(
        msg = message,
        code = code
    )