package io.github.feliperce.aabtoapk.utils.extractor

import java.util.UUID

data class ErrorMsg(
    val title: String = "",
    val msg: String = "",
    val id: String = UUID.randomUUID().toString()
)

data class SuccessMsg(
    val msg: String = "",
    val type: SuccessMsgType = SuccessMsgType.NONE,
    val id: String = UUID.randomUUID().toString()
)

enum class SuccessMsgType {
    NONE,
    EXTRACT_AAB,
    INSTALL_APKS
}