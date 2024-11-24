package io.github.feliperce.aabtoapk.viewmodel

import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import kotlinx.coroutines.flow.Flow
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

        val keystore = keystoreInfoDto?.let {
            aabExtractorRepository.uploadKeystore(
                keystoreInfoDto = it,
                hash = hash
            )
        }

        val uploadedFilesDto = aabExtractorRepository.uploadAab(
            fileName = fileName,
            fileBytes = fileBytes,
            hash = hash
        )

        val extractor = ApksExtractor(
            aabPath = uploadedFilesDto.path,
            outputApksPath = ServerConstants.PathConf.OUTPUT_EXTRACT_PATH,
            buildToolsPath = ServerConstants.PathConf.BUILD_TOOLS_PATH
        )

        return aabExtractorRepository.extract(
            uploadedFilesDto = uploadedFilesDto,
            extractor = extractor,
            keystoreInfoDto = keystore
        )
    }

}