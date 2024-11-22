package io.github.feliperce.aabtoapk

import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponse
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.io.File
import java.net.URLEncoder
import java.util.*

fun main() {
    ServerConstants.PathConf.mkdirs()

    embeddedServer(Netty, port = ServerConstants.PORT, host = ServerConstants.HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        json()
    }

    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AcceptEncoding)
        allowHeader(HttpHeaders.AcceptLanguage)
        allowHeader(HttpHeaders.CacheControl)
        allowHeader(HttpHeaders.AccessControlMaxAge)
        allowHeader(HttpHeaders.Connection)
        allowHeader(HttpHeaders.Host)
        allowHeader(HttpHeaders.Upgrade)
        allowHeader(HttpHeaders.UserAgent)
        anyHost()
    }

    routing {
        get("/") {
            this.call.respond("aaa")
        }

        post("/uploadAab") {
            var fileDescription = ""
            var fileName = ""
            var resultPath = ""
            var encodedDownloadUrl = ""
            var errorResponse: ErrorResponse? = null

            val multipartData = call.receiveMultipart(formFieldLimit = Long.MAX_VALUE)

            println("upload init")

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }

                    is PartData.FileItem -> {
                        println("upload start")

                        fileName = part.originalFileName?.substringBeforeLast(".") as String
                        val fileBytes = part.provider().readRemaining().readByteArray()

                        val uploadDir = File(ServerConstants.PathConf.OUTPUT_EXTRACT_PATH)

                        val cachedAab = File("${uploadDir.absolutePath}/$fileName")
                        cachedAab.writeBytes(fileBytes)

                        println("FILE CREATED -> ${cachedAab.absolutePath}")

                        val extractor = ApksExtractor(
                            aabPath = cachedAab.absolutePath,
                            outputApksPath = ServerConstants.PathConf.OUTPUT_EXTRACT_PATH,
                            buildToolsPath = ServerConstants.PathConf.BUILD_TOOLS_PATH
                        )

                        extractor.setSignConfig(
                            keystorePath = ServerConstants.DebugKeystore.PATH,
                            keystorePassword = ServerConstants.DebugKeystore.STORE_PASSWORD,
                            keyPassword = ServerConstants.DebugKeystore.KEY_PASSWORD,
                            keyAlias = ServerConstants.DebugKeystore.ALIAS,
                            onFailure = {
                                call.response.status(HttpStatusCode.NotAcceptable)
                                errorResponse = ErrorResponse(
                                    code = ErrorResponseType.KEYSTORE.code,
                                    message = it.msg
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

                                resultPath = path
                                fileName = "${name}.apks"
                                encodedDownloadUrl =
                                    "${ServerConstants.BASE_URL}/download/${URLEncoder.encode(fileName, "UTF-8")}"
                            },
                            onFailure = {
                                call.response.status(HttpStatusCode.NotAcceptable)
                                errorResponse = ErrorResponse(
                                    code = ErrorResponseType.EXTRACT.code,
                                    message = it.msg
                                )
                                println("AAB TO APKS FAIL -> ${it.msg}")
                            }
                        )

                        println("extracted -- $fileName")
                    }

                    else -> {
                        println("ELSE -0-")
                    }
                }
                part.dispose()
            }

            println("SAIU")

            errorResponse?.let {
                call.respond(it)
            } ?: run {
                call.respond(
                    AabConvertResponse(
                        fileName = fileName,
                        fileType = "apks",
                        downloadUrl = encodedDownloadUrl,
                        date = Date().time,
                        debugKeystore = true
                    )
                )
            }
        }

        get("/download/{fileName}") {
            val fileName = call.parameters["fileName"]

            if (fileName != null) {
                val file = File("${ServerConstants.PathConf.OUTPUT_EXTRACT_PATH}/$fileName")
                if (file.exists()) {
                    call.response.header(
                        HttpHeaders.ContentDisposition,
                        ContentDisposition.Attachment.withParameter(
                            ContentDisposition.Parameters.FileName, fileName
                        ).toString()
                    )
                    call.respondFile(file)
                    file.delete()
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(
                        ErrorResponse(
                            code = ErrorResponseType.DOWNLOAD_NOT_FOUND.code,
                            message = ErrorResponseType.DOWNLOAD_NOT_FOUND.msg
                        )
                    )
                }
            } else {
                call.response.status(HttpStatusCode.NotAcceptable)
                call.respond(
                    ErrorResponse(
                        code = ErrorResponseType.DOWNLOAD_INVALID_FILE_NAME.code,
                        message = ErrorResponseType.DOWNLOAD_INVALID_FILE_NAME.msg
                    )
                )
            }
        }
    }
}