package io.github.feliperce.aabtoapk.repository

import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AabExtractorRepository(
    private val uploadFilesDao: UploadFilesDao,
    private val extractedFilesDao: ExtractedFilesDao
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadAab(
        fileName: String,
        fileBytes: ByteArray
    ): UploadedFilesDto {
        return withContext(Dispatchers.IO) {
            val uploadDir = File(ServerConstants.PathConf.OUTPUT_EXTRACT_PATH)

            val hash = Uuid.random().toHexString()

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

    fun extract(
        uploadedFilesDto: UploadedFilesDto,
        extractor: ApksExtractor
    ) = callbackFlow<Resource<AabConvertResponse, ErrorResponse>> {
        extractor.setSignConfig(
            keystorePath = ServerConstants.DebugKeystore.PATH,
            keystorePassword = ServerConstants.DebugKeystore.STORE_PASSWORD,
            keyPassword = ServerConstants.DebugKeystore.KEY_PASSWORD,
            keyAlias = ServerConstants.DebugKeystore.ALIAS,
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
                        isDebugKeystore = true,
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
                            debugKeystore = true
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