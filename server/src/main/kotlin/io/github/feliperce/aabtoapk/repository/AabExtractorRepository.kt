package io.github.feliperce.aabtoapk.repository

import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.dao.ExtractedFilesDao
import io.github.feliperce.aabtoapk.data.local.dao.UploadFilesDao
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.utils.date.getCurrentDateTime
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File

class AabExtractorRepository(
    private val uploadFilesDao: UploadFilesDao,
    private val extractedFilesDao: ExtractedFilesDao
) {

    suspend fun uploadAab(
        fileName: String,
        hash: String,
        fileBytes: ByteArray
    ): UploadedFilesDto {
        return withContext(Dispatchers.IO) {
            val uploadDir = File(ServerConstants.PathConf.OUTPUT_EXTRACT_PATH)

            val cachedAab = File("${uploadDir.absolutePath}/${hash}.aab")
            cachedAab.writeBytes(fileBytes)

            uploadFilesDao.insert(
                UploadedFilesDto(
                    name = fileName,
                    path = cachedAab.absolutePath,
                    uploadedDate = getCurrentDateTime(),
                    formattedName = cachedAab.name
                )
            )
        }
    }

    suspend fun uploadKeystore(
        keystoreInfoDto: KeystoreInfoDto,
        hash: String
    ): KeystoreInfoDto {
        return withContext(Dispatchers.IO) {
            val uploadDir = File(ServerConstants.PathConf.KEYSTORE_PATH)

            val cachedKeystore = File("${uploadDir.absolutePath}/${hash}${keystoreInfoDto.fileExtension}")
            cachedKeystore.writeBytes(keystoreInfoDto.fileBytes)

            return@withContext keystoreInfoDto.copy(path = cachedKeystore.absolutePath)
        }
    }

    fun extract(
        uploadedFilesDto: UploadedFilesDto,
        keystoreInfoDto: KeystoreInfoDto?,
        extractor: ApksExtractor
    ) = callbackFlow<Resource<AabConvertResponse, ErrorResponse>> {

        val isDebugKeystore = keystoreInfoDto == null

        val keystore = if (isDebugKeystore) {
            KeystoreInfoDto(
                path = ServerConstants.DebugKeystore.PATH,
                fileExtension = ".keystore",
                keyAlias = ServerConstants.DebugKeystore.ALIAS,
                password = ServerConstants.DebugKeystore.STORE_PASSWORD,
                keyPassword = ServerConstants.DebugKeystore.KEY_PASSWORD,
                name = "",
                fileBytes = ByteArray(0)
            )
        } else {
            keystoreInfoDto!!
        }

        extractor.setSignConfig(
            keystorePath = keystore.path,
            keystorePassword = keystore.password,
            keyPassword = keystore.keyPassword,
            keyAlias = keystore.keyAlias,
            onFailure = {
                trySend(
                    Resource.Error(
                        error = ErrorResponseType.KEYSTORE.toErrorResponse(it.msg)
                    )
                )
                println("SET KEYSTORE FAIL -> ${it.msg}")
            }
        )

        println("SET KEYSTORE")
        extractor.aabToApks(
            aabFileName = uploadedFilesDto.formattedName,
            extractorOption = ApksExtractor.ExtractorOption.APKS,
            onSuccess =  { path, name ->
                println("AAB TO APKS success!!! -> ${path} || $name")

                val encodedDownloadUrl =
                    "${ServerConstants.BASE_URL}/download/$name"

                extractedFilesDao.insert(
                    extractedFilesDto = ExtractedFilesDto(
                        uploadedFileId = uploadedFilesDto.id,
                        name = uploadedFilesDto.name,
                        fileExtension = ".apks",
                        isDebugKeystore = isDebugKeystore,
                        extractedDate = getCurrentDateTime(),
                        downloadUrl = encodedDownloadUrl,
                        path = path,
                        formattedName = uploadedFilesDto.formattedName
                    )
                )

                trySend(
                    Resource.Success(
                        data = AabConvertResponse(
                            fileName = name,
                            fileType = ".apks",
                            downloadUrl = encodedDownloadUrl,
                            debugKeystore = isDebugKeystore
                        )
                    )
                )
            },
            onFailure = {
                trySend(
                    Resource.Error(
                        error = ErrorResponseType.EXTRACT.toErrorResponse(it.msg)
                    )
                )
                println("AAB TO APKS FAIL -> ${it.msg}")
            }
        )

        awaitClose { close() }
    }

}