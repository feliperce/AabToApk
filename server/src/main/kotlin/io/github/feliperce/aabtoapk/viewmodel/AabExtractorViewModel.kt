package io.github.feliperce.aabtoapk.viewmodel

import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import kotlinx.coroutines.flow.Flow
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AabExtractorViewModel(
    private val aabExtractorRepository: AabExtractorRepository
) {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun extract(
        fileName: String,
        fileBytes: ByteArray,
        keystoreInfoDto: KeystoreInfoDto?,
        extractorOption: ApksExtractor.ExtractorOption
    ): Flow<Resource<AabConvertResponse, ErrorResponse>> {
        val hash = Uuid.random().toHexString()

        val extractsFolder = File("${ServerConstants.PathConf.CACHE_PATH}/$hash")
        extractsFolder.mkdir()

        val keystore = keystoreInfoDto?.let {
            aabExtractorRepository.uploadKeystore(
                keystoreInfoDto = it,
                extractPath = extractsFolder.absolutePath
            )
        }

        val uploadedFilesDto = aabExtractorRepository.uploadAab(
            fileName = fileName,
            extractPath = extractsFolder.absolutePath,
            folderHash = hash,
            fileBytes = fileBytes
        )

        val extractor = ApksExtractor(
            aabPath = uploadedFilesDto.path,
            outputApksPath = extractsFolder.absolutePath,
            buildToolsPath = ServerConstants.PathConf.BUILD_TOOLS_PATH
        )

        return aabExtractorRepository.extract(
            uploadedFilesDto = uploadedFilesDto,
            extractor = extractor,
            keystoreInfoDto = keystore,
            folderHash = hash,
            extractorOption = extractorOption
        )
    }

    suspend fun getExtractedFileByHash(hash: String): ExtractedFilesDto? {
        return aabExtractorRepository.getExtractedFileByHash(hash)
    }

}