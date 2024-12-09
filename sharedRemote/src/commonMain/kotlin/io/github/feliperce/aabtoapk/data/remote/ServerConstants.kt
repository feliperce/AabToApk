package io.github.feliperce.aabtoapk.data.remote


object ServerConstants {
    const val PORT = 8080
    const val HOST = "localhost"
    const val BASE_URL = "http://$HOST:$PORT"
    const val MAX_AAB_UPLOAD_MB = 100
    const val REMOVE_UPLOAD_HOUR_TIME = 1

    object PathConf {
        const val CACHE_PATH = "/tmp/AabToApk"
        const val BUILD_TOOLS_PATH = "/home/felipe/Development/Android/Sdk/build-tools/35.0.0"
    }

    object DebugKeystore {
        const val PATH = "/home/felipe/.android/debug.keystore"
        const val ALIAS = "androiddebugkey"
        const val STORE_PASSWORD = "android"
        const val KEY_PASSWORD = "android"
    }
}
