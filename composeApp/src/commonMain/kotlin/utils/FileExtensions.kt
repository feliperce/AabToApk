package utils

import ca.gosyer.appdirs.AppDirs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

fun getUserDataDirPath(): String {
    val appDirs = AppDirs("AabToApk", "feliperce")
    return appDirs.getUserDataDir()
}

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