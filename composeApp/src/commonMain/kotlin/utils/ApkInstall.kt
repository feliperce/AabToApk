package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File

class ApkInstall(
    private val adbPath: String
) {

    suspend fun installApk(
        apkPath: String,
        onSuccess: () -> Unit,
        onFailure: (errorMsg: ErrorMsg) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            runCatching {
                execAndWait("./adb install \"$apkPath\"", File(adbPath))
                onSuccess()
            }.onFailure { error ->
                onFailure(
                    ErrorMsg(
                        title = "INSTALL APK ERROR",
                        msg = error.message ?: "Error on install APKS"
                    )
                )
            }
        }.await()
    }
}