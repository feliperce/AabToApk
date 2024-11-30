package io.github.feliperce.aabtoapk.feature.extractor.repository

import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ExtractorResponse
import io.github.feliperce.aabtoapk.feature.extractor.data.remote.ExtractorApi
import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import io.github.vinceglb.filekit.core.PlatformFile
import io.ktor.client.call.*
import io.ktor.http.*

class ExtractorRepository(
    private val extractorApi: ExtractorApi
) {

    suspend fun uploadAndExtract(
        keystoreDto: KeystoreDto,
        aabFileDto: AabFileDto
    ) {

        println("ENTROU repository UPLOAD")

        val response = extractorApi.uploadAndExtract(
            keystoreDto = keystoreDto,
            aabFileDto = aabFileDto
        )

        println("ENTROU repository UPLOAD - passou response")

        if (response.status == HttpStatusCode.OK) {
            val news = response.body() as ExtractorResponse
            println("SUCCESS")
            //emit(Resource.Success(data = news.toNews()))
        } else {
            println("FAILURE")
            val errorResponse = response.body() as ErrorResponse
            //emit(Resource.Error(error = errorResponse.toErrorData()))
        }

    }
}