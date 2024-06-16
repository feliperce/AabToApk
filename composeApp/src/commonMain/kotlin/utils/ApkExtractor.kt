package utils

import com.android.tools.build.bundletool.commands.BuildApksCommand
import com.android.tools.build.bundletool.model.Password
import com.android.tools.build.bundletool.model.SigningConfiguration
import kotlinx.coroutines.*
import java.nio.file.Paths
import java.util.*

class ApkExtractor(
    private val aabPath: String,
    private val outputApksPath: String
) {

    private var signingConfig: SigningConfiguration? = null

    suspend fun setSignConfig(
        keystorePath: String,
        keyAlias: String,
        keystorePassword: String,
        keyPassword: String,
        onFailure: (error: ErrorMsg) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            runCatching {
                signingConfig = SigningConfiguration.extractFromKeystore(
                    Paths.get(keystorePath),
                    keyAlias,
                    Optional.of(Password.createFromStringValue("pass:$keystorePassword")),
                    Optional.of(Password.createFromStringValue("pass:$keyPassword"))
                )
            }.onFailure { failure ->
                onFailure(
                    ErrorMsg(
                        title = "SIGN FAILURE",
                        msg = failure.message ?: "Keystore sign error"
                    )
                )
            }
        }.await()
    }

    suspend fun aabToApks(
        apksFileName: String = "",
        overwriteApks: Boolean,
        onSuccess: (output: String) -> Unit,
        onFailure: (errorMsg: ErrorMsg) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            if (signingConfig != null) {
                runCatching {
                    val newApksFileName = apksFileName.ifEmpty {
                        "extracted.apks"
                    }

                    val formattedOutputPath = outputApksPath.dropLastWhile { it == '/' }
                        .plus("/$newApksFileName.apks")

                    BuildApksCommand.builder()
                        .setBundlePath(Paths.get(aabPath))
                        .setSigningConfiguration(
                            signingConfig
                        )
                        .setVerbose(true)
                        .setOverwriteOutput(overwriteApks)
                        .setOutputFile(Paths.get(formattedOutputPath))
                        .build()
                        .execute()

                    onSuccess(formattedOutputPath)
                }.onFailure { failure ->
                    onFailure(
                        ErrorMsg(
                            title = "AAB EXTRACT FAILURE",
                            msg = failure.message ?: "Error on Extract aab"
                        )
                    )
                }
            }
        }.await()
    }
}