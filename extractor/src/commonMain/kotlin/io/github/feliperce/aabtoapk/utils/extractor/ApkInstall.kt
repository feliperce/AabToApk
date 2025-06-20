package io.github.feliperce.aabtoapk.utils.extractor

import io.github.feliperce.aabtoapk.utils.platform.execAndWait
import io.github.feliperce.aabtoapk.utils.platform.PlatformType
import io.github.feliperce.aabtoapk.utils.platform.PlatformUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File

class ApkInstall(
    private val adbPath: String
) {
    private val platformUtils = PlatformUtils()

    suspend fun installApk(
        apkPath: String,
        onSuccess: () -> Unit,
        onFailure: (errorMsg: ErrorMsg) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            runCatching {
                if (platformUtils.getPlatformType() == PlatformType.WINDOWS) {
                    execAndWait("install \"$apkPath\"", File("${adbPath}\\adb.exe"))
                } else {
                    execAndWait("./adb install \"$apkPath\"", File(adbPath))
                }
                onSuccess()
            }.onFailure { error ->
                onFailure(
                    ErrorMsg(
                        type = ErrorType.INSTALL_APK_ERROR,
                        msg = error.message ?: "Error on install APKS"
                    )
                )
            }
        }.await()
    }
}
