package io.github.feliperce.aabtoapk.utils.format

fun Int.convertMegaByteToBytesLong(): Long =
    (this * 1024 * 1024).toLong()