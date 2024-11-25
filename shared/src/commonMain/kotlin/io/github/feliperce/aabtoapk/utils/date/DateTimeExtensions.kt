package io.github.feliperce.aabtoapk.utils.date

import kotlinx.datetime.*


fun getCurrentInstant() =
    Clock.System.now()

fun Instant.addHour(hour: Int) =
    plus(1, DateTimeUnit.HOUR)