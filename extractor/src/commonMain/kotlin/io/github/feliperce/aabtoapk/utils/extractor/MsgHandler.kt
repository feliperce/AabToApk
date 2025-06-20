package io.github.feliperce.aabtoapk.utils.extractor

import java.util.UUID

enum class ErrorType {
    NONE,
    SIGN_FAILURE,
    AAB_EXTRACT_FAILURE,
    INSTALL_APK_ERROR,
    INVALID_SETTINGS
}

data class ErrorMsg(
    val type: ErrorType = ErrorType.NONE,
    val id: String = UUID.randomUUID().toString()
)

enum class SuccessMsgType {
    NONE,
    EXTRACT_AAB,
    INSTALL_APKS,
    SETTINGS_CHANGED
}

data class SuccessMsg(
    val type: SuccessMsgType = SuccessMsgType.NONE,
    val id: String = UUID.randomUUID().toString()
)
