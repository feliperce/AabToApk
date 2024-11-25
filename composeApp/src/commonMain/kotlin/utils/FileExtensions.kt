package utils

import ca.gosyer.appdirs.AppDirs

fun getUserDataDirPath(): String {
    val appDirs = AppDirs("AabToApk", "feliperce")
    return appDirs.getUserDataDir()
}

