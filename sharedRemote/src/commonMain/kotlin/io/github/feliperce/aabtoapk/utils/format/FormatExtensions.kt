package io.github.feliperce.aabtoapk.utils.format

fun Int.convertMegaByteToBytesLong(): Long =
    (this * 1024 * 1024).toLong()

fun String.replaceExtension(newExtension: String): String {
    val name = this.substringBeforeLast(".")

    return if (name != this) {
        name.plus(newExtension)
    } else {
        this
    }
}