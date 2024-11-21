package io.github.feliperce.aabtoapk.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: String = "",
    val code: String = "",
    val message: String = ""
)
