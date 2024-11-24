package io.github.feliperce.aabtoapk.utils.date

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun getCurrentDateTime() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())