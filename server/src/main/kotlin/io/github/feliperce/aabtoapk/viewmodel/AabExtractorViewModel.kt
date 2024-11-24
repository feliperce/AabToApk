package io.github.feliperce.aabtoapk.viewmodel

import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import kotlinx.coroutines.flow.Flow

class AabExtractorViewModel(
    private val aabExtractorRepository: AabExtractorRepository
) {

    suspend fun extract(
        fileName: String,
        fileBytes: ByteArray
    ): Flow<Resource<AabConvertResponse, ErrorResponse>> {
        val uploadedFilesDto = aabExtractorRepository.uploadAab(
            fileName = fileName,
            fileBytes = fileBytes
        )

        val extractor = ApksExtractor(
            aabPath = uploadedFilesDto.path,
            outputApksPath = ServerConstants.PathConf.OUTPUT_EXTRACT_PATH,
            buildToolsPath = ServerConstants.PathConf.BUILD_TOOLS_PATH
        )

        return aabExtractorRepository.extract(
            uploadedFilesDto = uploadedFilesDto,
            extractor = extractor
        )
    }

}