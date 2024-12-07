package io.github.feliperce.aabtoapk.feature.extractor.data.remote

import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ExtractorApi (
    private val client: HttpClient
) {
    suspend fun uploadAndExtract(
        keystoreDto: KeystoreDto,
        aabFileDto: AabFileDto
    ): HttpResponse {
        return client.submitFormWithBinaryData(
            url = "${ServerConstants.BASE_URL}/uploadAab",
            formData = formData {
                if (!keystoreDto.isDebugKeystore) {
                    append("alias", keystoreDto.alias)
                    append("keystorePassword", keystoreDto.password)
                    append("keyPassword", keystoreDto.keyPassword)
                    append("keystore", keystoreDto.keystoreByteArray, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"${keystoreDto.keystoreFileName}\"")
                    })
                }
                append("extractorOption", aabFileDto.extractorOption.id)
                append("aab", aabFileDto.aabByteArray, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"${aabFileDto.fileName}\"")
                })
            }
        )
    }
}