package io.github.feliperce.aabtoapk.data.remote

object ServerConstants {
    const val PORT = 8080
    const val HOST = "192.168.1.9"
    const val BASE_URL = "http://$HOST:$PORT"
    const val CACHE_PATH = "/tmp/AabToApk"
    const val EXTRACTOR_PATH = "/tmp/AabToApk/extracted"


    object DebugKeystore {
        const val PATH = "~/.android/debug.keystore"
        const val ALIAS = "androiddebugkey"
        const val STORE_PASSWORD = "android"
        const val KEY_PASSWORD = "android"
    }
}
