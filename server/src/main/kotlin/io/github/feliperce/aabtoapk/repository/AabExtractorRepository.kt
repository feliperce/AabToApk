package io.github.feliperce.aabtoapk.repository

import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import io.ktor.http.*
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.net.URLEncoder
import java.util.*

class AabExtractorRepository() {

    fun uploadAab(
        fileName: String,
        extractor: ApksExtractor
    ) = callbackFlow<Resource<AabConvertResponse, ErrorResponse>> {

        extractor.setSignConfig(
            keystorePath = ServerConstants.DebugKeystore.PATH,
            keystorePassword = ServerConstants.DebugKeystore.STORE_PASSWORD,
            keyPassword = ServerConstants.DebugKeystore.KEY_PASSWORD,
            keyAlias = ServerConstants.DebugKeystore.ALIAS,
            onFailure = {
                //call.response.status(HttpStatusCode.NotAcceptable)
                trySend(
                    Resource.Error(
                        error = ErrorResponse(
                            code = ErrorResponseType.KEYSTORE.code,
                            message = it.msg
                        )
                    )
                )
                println("SET KEYSTORE FAIL -> ${it.msg}")
            }
        )

        println("SET KEYSTORE")
        extractor.aabToApks(
            apksFileName = fileName,
            extractorOption = ApksExtractor.ExtractorOption.APKS,
            onSuccess =  { path, name ->
                println("AAB TO APKS success!!! -> ${path} || $name")

                val encodedDownloadUrl =
                    "${ServerConstants.BASE_URL}/download/${URLEncoder.encode(fileName, "UTF-8")}"
                trySend(
                    Resource.Success(
                        data = AabConvertResponse(
                            fileName = "${name}.apks",
                            fileType = "apks",
                            downloadUrl = encodedDownloadUrl,
                            date = Date().time,
                            debugKeystore = true
                        )
                    )
                )
            },
            onFailure = {
                //call.response.status(HttpStatusCode.NotAcceptable)
                trySend(
                    Resource.Error(
                        error = ErrorResponse(
                            code = ErrorResponseType.EXTRACT.code,
                            message = it.msg
                        )
                    )
                )
                println("AAB TO APKS FAIL -> ${it.msg}")
            }
        )
    }
}