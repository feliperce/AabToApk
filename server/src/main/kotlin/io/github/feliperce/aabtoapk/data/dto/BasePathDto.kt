package io.github.feliperce.aabtoapk.data.dto

import kotlinx.datetime.LocalDateTime

data class BasePathDto(
    val id: Int,
    val name: String,
    val createdDate: LocalDateTime,
    val dateToRemove: LocalDateTime
)
