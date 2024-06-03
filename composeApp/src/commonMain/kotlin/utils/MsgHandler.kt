package utils

import java.util.UUID

data class ErrorMsg(
    val title: String = "",
    val msg: String = "",
    val id: String = UUID.randomUUID().toString()
)

data class SuccessMsg(
    val msg: String = "",
    val id: String = UUID.randomUUID().toString()
)