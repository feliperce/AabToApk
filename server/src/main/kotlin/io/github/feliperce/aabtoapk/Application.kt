package io.github.feliperce.aabtoapk

import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.di.dataModule
import io.github.feliperce.aabtoapk.di.extractorModule
import io.github.feliperce.aabtoapk.utils.format.convertMegaByteToBytesLong
import io.github.feliperce.aabtoapk.viewmodel.AabExtractorViewModel
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
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import java.io.File

fun main() {
    ServerConstants.PathConf.mkdirs()

    embeddedServer(Netty, port = ServerConstants.PORT, host = ServerConstants.HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    install(Koin) {
        modules(
            dataModule, extractorModule
        )
    }

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

    val viewModel by inject<AabExtractorViewModel>()

    routing {


        get("/") {
            this.call.respond("aaa")
        }

        post("/uploadAab") {
            var alias = ""
            var keystorePassword = ""
            var keyPassword = ""
            var keystoreInfoDto: KeystoreInfoDto? = null

            val multipartData = call.receiveMultipart(formFieldLimit = 400.convertMegaByteToBytesLong())

            println("upload init")

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        when (part.name) {
                            "alias" -> alias = part.value
                            "keystorePassword" -> keystorePassword = part.value
                            "keyPassword" -> keyPassword = part.value
                        }
                    }

                    is PartData.FileItem -> {
                        println("upload start ${part.name}")

                        when (part.name) {
                            "keystore" -> {
                                println("START SEND KEYSTORE")
                                val fileName = part.originalFileName as String
                                println("KEYSTORE NAME $fileName")
                                val fileBytes = part.provider().readRemaining().readByteArray()

                                keystoreInfoDto = KeystoreInfoDto(
                                    keyAlias = alias,
                                    password = keystorePassword,
                                    keyPassword = keyPassword,
                                    name = fileName,
                                    fileBytes = fileBytes,
                                    fileExtension = ".${fileName.substringAfterLast(".")}"
                                )
                            }
                            "aab" -> {
                                val fileName = part.originalFileName as String
                                val fileBytes = part.provider().readRemaining().readByteArray()

                                viewModel.extract(
                                    fileName = fileName,
                                    fileBytes = fileBytes,
                                    keystoreInfoDto = keystoreInfoDto
                                ).collect { res ->
                                    when (res) {
                                        is Resource.Success -> {
                                            res.data?.let { data ->
                                                call.respond(data)
                                            } ?: run {
                                                val errorMsg =
                                                    "Something unexpected occurred while extracting, please try again later"
                                                call.respond(
                                                    ErrorResponseType.EXTRACT.toErrorResponse(errorMsg)
                                                )
                                            }
                                        }
                                        is Resource.Error -> {
                                            res.error?.let { error ->
                                                call.response.status(HttpStatusCode.NotAcceptable)
                                                call.respond(error)
                                            } ?: run {
                                                call.respond(
                                                    ErrorResponseType.UNKNOWN.toErrorResponse()
                                                )
                                            }
                                        }
                                        is Resource.Loading -> {}
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        println("ELSE -0-")
                    }
                }
                part.dispose()
            }
        }

        get("/download/{hash}") {
            val hash = call.parameters["hash"]

            if (hash != null) {
                val extractedFileDto = viewModel.getExtractedFileByHash(hash)

                if (extractedFileDto != null) {
                    val file = File(extractedFileDto.path)

                    if (file.exists()) {
                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Attachment.withParameter(
                                ContentDisposition.Parameters.FileName, extractedFileDto.name
                            ).toString()
                        )
                        call.respondFile(file)
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(ErrorResponseType.DOWNLOAD_NOT_FOUND.toErrorResponse())
                    }
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                    call.respond(ErrorResponseType.DOWNLOAD_NOT_FOUND.toErrorResponse())
                }
            } else {
                call.response.status(HttpStatusCode.NotAcceptable)
                call.respond(ErrorResponseType.DOWNLOAD_INVALID_FILE_NAME.toErrorResponse())
            }
        }
    }
}