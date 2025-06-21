package io.github.feliperce.aabtoapk.data.remote

import AabToApk.sharedRemote.BuildConfig

object ServerConstants {
    const val PROXY_HOST = BuildConfig.PROXY_HOST
    const val PROXY_BASE_URL = "https://$PROXY_HOST"
    const val MAX_AAB_UPLOAD_MB = 100
}
