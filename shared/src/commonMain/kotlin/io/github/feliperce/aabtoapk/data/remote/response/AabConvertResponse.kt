package io.github.feliperce.aabtoapk.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AabConvertResponse(
    val fileName: String,
    val fileType: String,
    val downloadUrl: String,
    val debugKeystore: Boolean,
    val date: Long
)

