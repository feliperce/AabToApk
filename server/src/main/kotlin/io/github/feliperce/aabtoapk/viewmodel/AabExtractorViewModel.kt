package io.github.feliperce.aabtoapk.viewmodel

import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.net.URLEncoder

class AabExtractorViewModel(
    private val aabExtractorRepository: AabExtractorRepository
) {

    fun uploadAab(
        fileName: String,
        fileBytes: ByteArray
    ): Flow<Resource<AabConvertResponse, ErrorResponse>> {
        val uploadDir = File(ServerConstants.PathConf.OUTPUT_EXTRACT_PATH)

        val cachedAab = File("${uploadDir.absolutePath}/$fileName")
        cachedAab.writeBytes(fileBytes)

        println("FILE CREATED -> ${cachedAab.absolutePath}")

        val extractor = ApksExtractor(
            aabPath = cachedAab.absolutePath,
            outputApksPath = ServerConstants.PathConf.OUTPUT_EXTRACT_PATH,
            buildToolsPath = ServerConstants.PathConf.BUILD_TOOLS_PATH
        )

        return aabExtractorRepository.uploadAab(
            extractor = extractor,
            fileName = fileName
        )
    }

}