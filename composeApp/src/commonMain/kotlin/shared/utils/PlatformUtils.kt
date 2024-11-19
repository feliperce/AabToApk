package shared.utils

import shared.utils.PlatformType.*

class PlatformUtils {

    fun getPlatformType(): PlatformType {
        val jvmOs = System.getProperty("os.name").lowercase()

        return if (jvmOs.contains("win")) {
            WINDOWS
        } else if (jvmOs.contains("nix") || jvmOs.contains("nux") || jvmOs.contains("aix")) {
            LINUX
        } else if (jvmOs.contains("mac")) {
            MACOS
        } else if (jvmOs.contains("android")) {
            ANDROID
        } else {
            UNKNOWN
        }
    }

    fun getPlatformExtension(): String {
        val currentOs = getPlatformType()

        return if (currentOs == WINDOWS) {
            ".exe"
        } else {
            ""
        }
    }
}