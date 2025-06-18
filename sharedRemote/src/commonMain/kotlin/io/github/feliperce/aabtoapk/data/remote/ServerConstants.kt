package io.github.feliperce.aabtoapk.data.remote

import AabToApk.sharedRemote.BuildConfig

object ServerConstants {
    const val PORT = BuildConfig.PORT
    const val HOST = BuildConfig.HOST
    const val PROXY_HOST = BuildConfig.PROXY_HOST
    const val BASE_URL = "http://$HOST:$PORT"
    const val PROXY_BASE_URL = "https://$PROXY_HOST"
    val MAX_AAB_UPLOAD_MB = BuildConfig.MAX_AAB_UPLOAD_MB.toInt()
    val REMOVE_UPLOAD_HOUR_TIME = BuildConfig.REMOVE_UPLOAD_HOUR_TIME.toInt()

    object PathConf {
        val CACHE_PATH = BuildConfig.CACHE_PATH
        val BUILD_TOOLS_PATH = BuildConfig.BUILD_TOOLS_PATH
    }

    object DebugKeystore {
        val PATH = BuildConfig.DEBUG_KEYSTORE_PATH
        val ALIAS = BuildConfig.DEBUG_KEYSTORE_ALIAS
        val STORE_PASSWORD = BuildConfig.DEBUG_KEYSTORE_STORE_PASSWORD
        val KEY_PASSWORD = BuildConfig.DEBUG_KEYSTORE_KEY_PASSWORD
    }
}
