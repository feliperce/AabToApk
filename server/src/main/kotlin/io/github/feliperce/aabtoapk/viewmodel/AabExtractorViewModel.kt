package io.github.feliperce.aabtoapk.viewmodel

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
        keystoreInfoDto: KeystoreInfoDto?
    ): Flow<Resource<AabConvertResponse, ErrorResponse>> {
        val hash = Uuid.random().toHexString()

        val extractsFolder = File("${ServerConstants.PathConf.CACHE_PATH}/$hash")
        extractsFolder.mkdir()

        val keystore = keystoreInfoDto?.let {
            aabExtractorRepository.uploadKeystore(
                keystoreInfoDto = it,
                extractPath = extractsFolder.absolutePath,
                hash = hash
            )
        }

        val uploadedFilesDto = aabExtractorRepository.uploadAab(
            fileName = fileName,
            extractPath = extractsFolder.absolutePath,
            fileBytes = fileBytes,
            hash = hash
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
            hash = hash
        )
    }

}