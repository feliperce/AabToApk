package io.github.feliperce.aabtoapk.repository

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.dao.BasePathDao
import io.github.feliperce.aabtoapk.data.local.dao.ExtractedFilesDao
import io.github.feliperce.aabtoapk.data.local.dao.UploadFilesDao
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.utils.date.getCurrentDateTime
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import io.github.feliperce.aabtoapk.utils.format.replaceExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AabExtractorRepository(
    private val uploadFilesDao: UploadFilesDao,
    private val extractedFilesDao: ExtractedFilesDao,
    private val basePathDao: BasePathDao
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadAab(
        fileName: String,
        extractPath: String,
        folderHash: String,
        fileBytes: ByteArray
    ): UploadedFilesDto {
        return withContext(Dispatchers.IO) {
            val hash = Uuid.random().toHexString()

            val cachedAab = File("${extractPath}/${hash}")
            cachedAab.writeBytes(fileBytes)

            uploadFilesDao.insert(
                UploadedFilesDto(
                    name = fileName,
                    path = cachedAab.absolutePath,
                    uploadedDate = getCurrentDateTime(),
                    formattedName = cachedAab.name,
                    hash = folderHash
                )
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadKeystore(
        keystoreInfoDto: KeystoreInfoDto,
        extractPath: String
    ): KeystoreInfoDto {
        return withContext(Dispatchers.IO) {
            val hash = Uuid.random().toHexString()

            val cachedKeystore = File("${extractPath}/${hash}")
            cachedKeystore.writeBytes(keystoreInfoDto.fileBytes)

            return@withContext keystoreInfoDto.copy(path = cachedKeystore.absolutePath)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun extract(
        uploadedFilesDto: UploadedFilesDto,
        keystoreInfoDto: KeystoreInfoDto?,
        basePathDto: BasePathDto,
        folderHash: String,
        extractor: ApksExtractor,
        extractorOption: ApksExtractor.ExtractorOption
    ) = callbackFlow<Resource<AabConvertResponse, ErrorResponse>> {

        val hash = Uuid.random().toHexString()
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
            apksFileName = hash,
            extractorOption = extractorOption,
            onSuccess =  { path, name ->
                println("AAB TO APKS success!!! -> ${path} || $name")

                val encodedDownloadUrl =
                    "${ServerConstants.BASE_URL}/download/$folderHash"

                val realName = uploadedFilesDto.name.replaceExtension(extractorOption.extension)

                extractedFilesDao.insert(
                    extractedFilesDto = ExtractedFilesDto(
                        uploadedFileId = uploadedFilesDto.id,
                        name = realName,
                        fileExtension = extractorOption.extension,
                        isDebugKeystore = isDebugKeystore,
                        extractedDate = getCurrentDateTime(),
                        downloadUrl = encodedDownloadUrl,
                        path = path,
                        hash = folderHash,
                        formattedName = uploadedFilesDto.formattedName,
                    )
                )

                trySend(
                    Resource.Success(
                        data = AabConvertResponse(
                            fileName = realName,
                            fileType = extractorOption.extension,
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

    suspend fun getExtractedFileByHash(hash: String): ExtractedFilesDto? {
        return withContext(Dispatchers.IO) {
            extractedFilesDao.getByHash(hash)
        }
    }

}