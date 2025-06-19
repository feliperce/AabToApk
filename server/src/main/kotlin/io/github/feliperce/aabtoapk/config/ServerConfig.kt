package io.github.feliperce.aabtoapk.config

object ServerConfig {
    object PathConf {
        val CACHE_PATH: String = System.getenv("AAB_CACHE_PATH") ?: "/tmp/AabToApk"
        val BUILD_TOOLS_PATH: String = System.getenv("AAB_BUILD_TOOLS_PATH") ?: ""

        fun mkdirs() {
            val cacheDir = java.io.File(CACHE_PATH)
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
        }
    }

    object DebugKeystore {
        val PATH: String = System.getenv("DEBUG_KEYSTORE_PATH") ?: ""
        val ALIAS: String = System.getenv("DEBUG_KEYSTORE_ALIAS") ?: "androiddebugkey"
        val STORE_PASSWORD: String = System.getenv("DEBUG_KEYSTORE_STORE_PASSWORD") ?: "android"
        val KEY_PASSWORD: String = System.getenv("DEBUG_KEYSTORE_KEY_PASSWORD") ?: "android"
    }

    object Database {
        val HOST: String = System.getenv("SV_DB_HOST") ?: "localhost"
        val PORT: String = System.getenv("SV_DB_PORT") ?: "5432"
        val NAME: String = System.getenv("SV_DB_NAME") ?: "AabToApk"
        val USER: String = System.getenv("SV_DB_USER") ?: ""
        val PASSWORD: String = System.getenv("SV_DB_PASSWORD") ?: ""

        val URL: String = "jdbc:postgresql://$HOST:$PORT/$NAME"
    }

    val REMOVE_UPLOAD_HOUR_TIME: Int = System.getenv("AAB_REMOVE_UPLOAD_HOUR_TIME")?.toIntOrNull() ?: 1

    val AUTH_TOKEN: String = System.getenv("AUTH_TOKEN") ?: ""
}
