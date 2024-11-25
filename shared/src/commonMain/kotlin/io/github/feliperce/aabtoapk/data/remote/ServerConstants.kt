package io.github.feliperce.aabtoapk.data.remote

import java.io.File

object ServerConstants {
    const val PORT = 8080
    const val HOST = "192.168.1.9"
    const val BASE_URL = "http://$HOST:$PORT"
    const val MAX_AAB_UPLOAD_MB = 400
    const val MAX_JKS_UPLOAD_MB = 1

    object PathConf {
        const val CACHE_PATH = "/tmp/AabToApk"
        const val BUILD_TOOLS_PATH = "/home/felipe/Development/Android/Sdk/build-tools/35.0.0"

        fun mkdirs() {
            File(CACHE_PATH).mkdirs()
        }
    }

    object DebugKeystore {
        const val PATH = "/home/felipe/.android/debug.keystore"
        const val ALIAS = "androiddebugkey"
        const val STORE_PASSWORD = "android"
        const val KEY_PASSWORD = "android"
    }
}
