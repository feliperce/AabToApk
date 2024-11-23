package io.github.feliperce.aabtoapk.utils.extractor

import com.android.tools.build.bundletool.androidtools.Aapt2Command
import com.android.tools.build.bundletool.commands.BuildApksCommand
import com.android.tools.build.bundletool.model.Password
import com.android.tools.build.bundletool.model.SigningConfiguration
import io.github.feliperce.aabtoapk.utils.platform.PlatformUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.zip.ZipFile

class ApksExtractor(
    private val aabPath: String,
    private val outputApksPath: String,
    private val buildToolsPath: String
) {

    private var signingConfig: SigningConfiguration? = null
    private val platformUtils = PlatformUtils()

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
        aabFileName: String = "",
        extractorOption: ExtractorOption,
        onSuccess: (output: String, fileName: String) -> Unit,
        onFailure: (errorMsg: ErrorMsg) -> Unit
    ) = withContext(Dispatchers.IO) {
        async {
            if (signingConfig != null) {
                runCatching {
                    val newApksFileName = aabFileName.ifEmpty {
                        "extracted"
                    }.substringBeforeLast(".")
                        .plus(".apks")

                    val formattedAabFileName = aabFileName.substringBeforeLast(".")

                    val formattedOutputPath = outputApksPath.dropLastWhile { it == '/' }
                        .plus("/$newApksFileName")

                    val aapt2Path = buildToolsPath.dropLastWhile { it == '/' }
                        .plus("/aapt2${platformUtils.getPlatformExtension()}")

                    BuildApksCommand.builder()
                        .setBundlePath(Paths.get(aabPath))
                        .setSigningConfiguration(
                            signingConfig
                        )
                        .setAapt2Command(Aapt2Command.createFromExecutablePath(Paths.get(aapt2Path)))
                        .setOverwriteOutput(true)
                        .setOutputFile(Paths.get(formattedOutputPath))
                        .setApkBuildMode(extractorOption.toApkBuildMode())
                        .build()
                        .execute()

                    val absolutPath = if (extractorOption == ExtractorOption.UNIVERSAL_APK) {
                        unzipUniversalApks(
                            formattedOutputPath,
                            formattedAabFileName
                        )
                    } else {
                        formattedOutputPath
                    }

                    onSuccess(absolutPath, newApksFileName)
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

    private suspend fun unzipUniversalApks(
        apksPath: String,
        apksFileName: String
    ): String {
        return withContext(Dispatchers.IO) {
            val zipFile = ZipFile(apksPath)
            val universalApkEntry = zipFile.entries().asSequence()
                .find { it.name == "universal.apk" }

            universalApkEntry?.let { entry ->
                zipFile.getInputStream(entry).use { input ->
                    val outputFilePath = outputApksPath.trimEnd('/') + "/$apksFileName-(universal).apk"
                    val outputFile = File(outputFilePath)

                    outputFile.outputStream().buffered().use { output ->
                        input.copyTo(output, bufferSize = 4096)
                    }

                    File(apksPath).delete()

                    return@withContext outputFile.absolutePath
                }
            }

            return@withContext ""
        }
    }

    enum class ExtractorOption {
        APKS,
        UNIVERSAL_APK
    }

    private fun ExtractorOption.toApkBuildMode() =
        when (this) {
            ExtractorOption.UNIVERSAL_APK -> {
                BuildApksCommand.ApkBuildMode.UNIVERSAL
            }
            else -> BuildApksCommand.ApkBuildMode.DEFAULT
        }
}