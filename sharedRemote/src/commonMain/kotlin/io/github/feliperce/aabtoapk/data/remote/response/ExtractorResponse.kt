package io.github.feliperce.aabtoapk.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ExtractorResponse(
    val fileName: String,
    val fileType: String,
    val downloadUrl: String,
    val debugKeystore: Boolean
)

