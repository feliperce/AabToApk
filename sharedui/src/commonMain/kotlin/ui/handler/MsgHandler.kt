package ui.handler

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class DefaultErrorType {
    NONE,
    GENERIC,
    KEYSTORE_PASSWORD,
    KEYSTORE_KEY_ALIAS,
    KEYSTORE_KEY_PASSWORD,
    KEYSTORE_FILE,
    AAB_FILE,
    MAX_FILE_SIZE
}

@OptIn(ExperimentalUuidApi::class)
data class DefaultErrorMsg(
    val type: DefaultErrorType = DefaultErrorType.NONE,
    val id: String = Uuid.random().toHexString(),
    val code: Int = -1
)

enum class DefaultSuccessType {
    NONE,
    EXTRACT_AAB
}

@OptIn(ExperimentalUuidApi::class)
data class DefaultSuccessMsg(
    val type: DefaultSuccessType = DefaultSuccessType.NONE,
    val id: String = Uuid.random().toHexString()
)
