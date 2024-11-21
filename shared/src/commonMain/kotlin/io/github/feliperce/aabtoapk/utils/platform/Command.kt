package io.github.feliperce.aabtoapk.utils.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun execAndWait(command: String, dir: File? = null): Int {
    return withContext(Dispatchers.IO) {
        ProcessBuilder("/bin/sh", "-c", command)
            .redirectErrorStream(true)
            .inheritIO()
            .directory(dir)
            .start()
            .waitFor()
    }
}