package utils

import com.android.bundle.Commands.ApkDescription
import com.android.ddmlib.Log
import com.android.tools.build.bundletool.commands.BuildApksCommand
import com.android.tools.build.bundletool.commands.InstallApksCommand
import com.android.tools.build.bundletool.device.DdmlibAdbServer
import com.android.tools.build.bundletool.model.ApkListener
import com.android.tools.build.bundletool.model.Password
import com.android.tools.build.bundletool.model.SigningConfiguration
import kotlinx.coroutines.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class ApkExtractor(
    private val adbPath: String,
    private val aabPath: String,
    private val outputApksPath: String
) {

    private var signingConfig: SigningConfiguration? = null

    suspend fun setSignConfig(
        keystorePath: String,
        keyAlias: String,
        keystorePassword: String,
        keyPassword: String,
        onFailure: (errorTitle: String, errorMsg: String) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            runCatching {
                signingConfig = SigningConfiguration.extractFromKeystore(
                    Paths.get(keystorePath),
                    keyAlias,
                    Optional.of(Password.createFromStringValue("pass:$keystorePassword")),
                    Optional.of(Password.createFromStringValue("pass:$keyPassword"))
                )
            }.onFailure { error ->
                onFailure("SIGN FAILURE", error.message ?: "Keystore sign error")
            }
        }.await()
    }

    suspend fun aabToApks(
        apksFileName: String = "",
        onSuccess: () -> Unit,
        onFailure: (errorTitle: String, errorMsg: String) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            if (signingConfig != null) {
                runCatching {
                    val newApksFileName = apksFileName.ifEmpty {
                        "extracted.apks"
                    }

                    println("extracted - $newApksFileName")
                    val formattedOutputPath = outputApksPath.dropLastWhile { it == '/' }.plus("/$newApksFileName.apks")
                    println("extracted - $formattedOutputPath")

                    BuildApksCommand.builder()
                        .setBundlePath(Paths.get(aabPath))
                        .setSigningConfiguration(
                            signingConfig
                        )
                        .setVerbose(true)
                        .setOutputFile(Paths.get(formattedOutputPath))
                        .build()
                        .execute()

                    onSuccess()
                }.onFailure { failure ->
                    onFailure("AAB EXTRACT FAILURE", failure.message ?: "Error on Extract aab")
                }
            } else {
                onFailure("KEYSTORE FAILURE","Wrong keystore settings")
            }
        }.await()
    }

    suspend fun installApks(
        onSuccess: () -> Unit,
        onFailure: (errorTitle: String, errorMsg: String) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            runCatching {
                InstallApksCommand.builder()
                    .setAdbPath(Path.of(adbPath))
                    .setAdbServer(DdmlibAdbServer.getInstance())
                    .setApksArchivePath(Path.of(outputApksPath))
                    .build().execute()

                onSuccess()
            }.onFailure { error ->
                onFailure("INSTALL APK ERROR", error.message ?: "Error on install APKS")
            }
        }.await()
    }
}