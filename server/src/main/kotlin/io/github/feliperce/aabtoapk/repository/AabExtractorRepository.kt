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
import io.github.feliperce.aabtoapk.data.remote.response.ExtractorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.utils.date.addHour
import io.github.feliperce.aabtoapk.utils.date.getCurrentInstant
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
    suspend fun insertBasePath(): BasePathDto {
        return withContext(Dispatchers.IO) {
            val hash = Uuid.random().toHexString()

            val extractsFolder = File("${ServerConstants.PathConf.CACHE_PATH}/$hash")
            extractsFolder.mkdir()

            val currentInstant = getCurrentInstant()

            return@withContext basePathDao.insert(
                BasePathDto(
                    name = hash,
                    path = extractsFolder.absolutePath,
                    createdDate = currentInstant,
                    dateToRemove = currentInstant.addHour(ServerConstants.REMOVE_UPLOAD_HOUR_TIME)
                )
            )
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadAab(
        fileName: String,
        basePathDto: BasePathDto,
        fileBytes: ByteArray
    ): UploadedFilesDto {
        return withContext(Dispatchers.IO) {
            val hash = Uuid.random().toHexString()

            val cachedAab = File("${basePathDto.path}/${hash}")
            cachedAab.writeBytes(fileBytes)

            uploadFilesDao.insert(
                UploadedFilesDto(
                    name = fileName,
                    path = cachedAab.absolutePath,
                    uploadedDate = getCurrentInstant(),
                    formattedName = cachedAab.name,
                    basePathDto = basePathDto
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
        extractor: ApksExtractor,
        extractorOption: ApksExtractor.ExtractorOption
    ) = callbackFlow<Resource<ExtractorResponse, ErrorResponse>> {

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
            keystoreInfoDto
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

        extractor.aabToApks(
            apksFileName = hash,
            extractorOption = extractorOption,
            onSuccess =  { path, name ->
                println("AAB TO APKS success!!! -> ${path} || $name")

                val encodedDownloadUrl =
                    "${ServerConstants.PROXY_BASE_URL}/download/${uploadedFilesDto.basePathDto.name}"

                val realName = uploadedFilesDto.name.replaceExtension(extractorOption.extension)

                extractedFilesDao.insert(
                    extractedFilesDto = ExtractedFilesDto(
                        name = realName,
                        fileExtension = extractorOption.extension,
                        isDebugKeystore = isDebugKeystore,
                        extractedDate = getCurrentInstant(),
                        downloadUrl = encodedDownloadUrl,
                        path = path,
                        formattedName = uploadedFilesDto.formattedName,
                        uploadedFilesDto = uploadedFilesDto,
                        basePathDto = uploadedFilesDto.basePathDto
                    )
                )

                trySend(
                    Resource.Success(
                        data = ExtractorResponse(
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

    suspend fun getBasePathByName(name: String): BasePathDto? {
        return withContext(Dispatchers.IO) {
            basePathDao.getByName(name)
        }
    }

    suspend fun getExtractedFileByBasePath(basePathDto: BasePathDto): ExtractedFilesDto? {
        return withContext(Dispatchers.IO) {
            extractedFilesDao.getByBasePath(basePathDto)
        }
    }

}