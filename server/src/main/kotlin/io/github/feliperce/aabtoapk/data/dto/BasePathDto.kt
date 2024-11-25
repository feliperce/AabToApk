package io.github.feliperce.aabtoapk.data.dto

import kotlinx.datetime.Instant

data class BasePathDto(
    val id: Int = -1,
    val name: String,
    val path: String,
    val createdDate: Instant,
    val dateToRemove: Instant
)
