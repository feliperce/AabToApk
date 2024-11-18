package utils

import com.android.tools.build.bundletool.commands.InstallApksCommand
import com.android.tools.build.bundletool.device.DdmlibAdbServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import shared.utils.PlatformUtils
import java.nio.file.Path

class ApksInstall(
    private val adbPath: String
) {
    private val platformUtils = PlatformUtils()

    suspend fun installApks(
        apksPath: String,
        onSuccess: () -> Unit,
        onFailure: (errorMsg: ErrorMsg) -> Unit
    ) = withContext(Dispatchers.IO) {

        val formattedAdbPath = adbPath.dropLastWhile { it == '/' }
            .plus("/adb${platformUtils.getPlatformExtension()}")

        async {
            runCatching {
                InstallApksCommand.builder()
                    .setAdbPath(Path.of(formattedAdbPath))
                    .setAdbServer(DdmlibAdbServer.getInstance())
                    .setApksArchivePath(Path.of(apksPath))
                    .build().execute()

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