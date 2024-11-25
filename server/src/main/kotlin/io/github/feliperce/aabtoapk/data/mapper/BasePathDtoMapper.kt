package io.github.feliperce.aabtoapk.data.mapper

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.local.entity.BasePathEntity

fun BasePathEntity.toBasePathDto() =
    BasePathDto(
        id = id.value,
        name = name,
        path = path,
        createdDate = createdDate,
        dateToRemove = dateToRemove
    )