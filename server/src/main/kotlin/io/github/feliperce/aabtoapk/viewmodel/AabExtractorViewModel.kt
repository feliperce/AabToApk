package io.github.feliperce.aabtoapk.viewmodel

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.ExtractorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import kotlinx.coroutines.flow.Flow

class AabExtractorViewModel(
    private val aabExtractorRepository: AabExtractorRepository
) {

    suspend fun extract(
        fileName: String,
        fileBytes: ByteArray,
        keystoreInfoDto: KeystoreInfoDto?,
        extractorOption: ApksExtractor.ExtractorOption
    ): Flow<Resource<ExtractorResponse, ErrorResponse>> {

        val basePathDto = aabExtractorRepository.insertBasePath()

        val keystore = keystoreInfoDto?.let {
            aabExtractorRepository.uploadKeystore(
                keystoreInfoDto = it,
                extractPath = basePathDto.path
            )
        }

        val uploadedFilesDto = aabExtractorRepository.uploadAab(
            fileName = fileName,
            basePathDto = basePathDto,
            fileBytes = fileBytes
        )

        val extractor = ApksExtractor(
            aabPath = uploadedFilesDto.path,
            outputApksPath = basePathDto.path,
            buildToolsPath = ServerConstants.PathConf.BUILD_TOOLS_PATH
        )

        return aabExtractorRepository.extract(
            uploadedFilesDto = uploadedFilesDto,
            extractor = extractor,
            keystoreInfoDto = keystore,
            extractorOption = extractorOption
        )
    }

    suspend fun getBasePathByName(name: String): BasePathDto? {
        return aabExtractorRepository.getBasePathByName(name)
    }

    suspend fun getExtractedByBasePath(basePathDto: BasePathDto): ExtractedFilesDto? {
        return aabExtractorRepository.getExtractedFileByBasePath(basePathDto)
    }
}