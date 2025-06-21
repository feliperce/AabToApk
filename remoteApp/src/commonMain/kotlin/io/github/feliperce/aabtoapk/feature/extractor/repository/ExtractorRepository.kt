package io.github.feliperce.aabtoapk.feature.extractor.repository

import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ExtractorResponse
import io.github.feliperce.aabtoapk.feature.extractor.data.remote.ExtractorApi
import io.github.feliperce.aabtoapk.feature.extractor.mapper.toErrorMsg
import io.github.feliperce.aabtoapk.feature.extractor.mapper.toExtractorResponseDto
import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorResponseDto
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import io.github.vinceglb.filekit.core.PlatformFile
import io.ktor.client.call.*
import io.ktor.http.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import ui.handler.DefaultErrorMsg
import ui.handler.DefaultErrorType

class ExtractorRepository(
    private val extractorApi: ExtractorApi
) {

    suspend fun uploadAndExtract(
        keystoreDto: KeystoreDto,
        aabFileDto: AabFileDto
    ) = flow<Resource<ExtractorResponseDto, DefaultErrorMsg>> {
        val response = extractorApi.uploadAndExtract(
            keystoreDto = keystoreDto,
            aabFileDto = aabFileDto
        )

        if (response.status == HttpStatusCode.OK) {
            val extractorResponse = response.body() as ExtractorResponse
            emit(Resource.Success(data = extractorResponse.toExtractorResponseDto()))
        } else {
            val errorResponse = response.body() as ErrorResponse
            emit(Resource.Error(error = errorResponse.toErrorMsg()))
        }
    }.onStart {
        emit(Resource.Loading(isLoading = true))
    }.onCompletion {
        emit(Resource.Loading(isLoading = false))
    }.catch { cause ->
        println(cause.toString())
        emit(
            Resource.Error(
                error = DefaultErrorMsg(
                    type = DefaultErrorType.GENERIC
                )
            )
        )
        emit(Resource.Loading(isLoading = false))
    }
}