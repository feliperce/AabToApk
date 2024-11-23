package io.github.feliperce.aabtoapk.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: Int = -1,
    val message: String = ""
)

enum class ErrorResponseType(val code: Int, val msg: String) {
    KEYSTORE(1000, "Invalid keystore"),
    EXTRACT(2000, "Error with extractor"),
    DOWNLOAD_NOT_FOUND(3000, "File not found"),
    DOWNLOAD_INVALID_FILE_NAME(3001, "Invalid file name"),
    UNKNOWN(0, "An unexpected error occurred, please return later");

    fun toErrorResponse(customMsg: String? = null): ErrorResponse {
        return ErrorResponse(
            code = code,
            message = customMsg ?: msg
        )
    }
}

