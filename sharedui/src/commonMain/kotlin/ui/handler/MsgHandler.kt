package ui.handler

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class DefaultErrorMsg(
    val title: String = "",
    val msg: String = "",
    val id: String = Uuid.random().toHexString()
)

@OptIn(ExperimentalUuidApi::class)
data class DefaultSuccessMsg(
    val id: String = Uuid.random().toHexString(),
    val msg: String = ""
)