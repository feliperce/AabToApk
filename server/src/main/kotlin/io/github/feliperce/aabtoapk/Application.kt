package io.github.feliperce.aabtoapk

import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.data.remote.Resource
import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.ErrorResponseType
import io.github.feliperce.aabtoapk.di.dataModule
import io.github.feliperce.aabtoapk.di.extractorModule
import io.github.feliperce.aabtoapk.job.initJobs
import io.github.feliperce.aabtoapk.server.BuildConfig
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import io.github.feliperce.aabtoapk.utils.format.convertMegaByteToBytesLong
import io.github.feliperce.aabtoapk.utils.mkdirs
import io.github.feliperce.aabtoapk.utils.sendErrorResponse
import io.github.feliperce.aabtoapk.viewmodel.AabExtractorViewModel
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
import sun.security.util.KeyUtil.validate
import java.io.File

fun main() {
    ServerConstants.PathConf.mkdirs()

    embeddedServer(
        factory = Netty,
        port = ServerConstants.PORT,
        host = ServerConstants.HOST,
        module = Application::module
    ).start(wait = true)
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
        allowMethod(HttpMethod.Post)
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

    install(Authentication) {
        bearer {
            realm = "Wasm access"
            authenticate { tokenCredential ->
                if (tokenCredential.token == BuildConfig.AUTH_TOKEN) {
                    UserIdPrincipal("kwasm")
                } else {
                    this.respond(
                        ErrorResponseType.WRONG_API_KEY.toErrorResponse()
                    )
                }
            }
        }
    }

    initJobs()

    val viewModel by inject<AabExtractorViewModel>()

    routing {

        authenticate {
            post("/uploadAab") {
                var alias = ""
                var keystorePassword = ""
                var keyPassword = ""
                var extractorOption = ApksExtractor.ExtractorOption.APKS
                var keystoreInfoDto: KeystoreInfoDto? = null

                val multipartData = call.receiveMultipart(
                    formFieldLimit = ServerConstants.MAX_AAB_UPLOAD_MB.convertMegaByteToBytesLong()
                )

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            when (part.name) {
                                "alias" -> alias = part.value
                                "keystorePassword" -> keystorePassword = part.value
                                "keyPassword" -> keyPassword = part.value
                                "extractorOption" -> {
                                    extractorOption = when (part.value) {
                                        ApksExtractor.ExtractorOption.APKS.id -> {
                                            ApksExtractor.ExtractorOption.APKS
                                        }

                                        ApksExtractor.ExtractorOption.UNIVERSAL_APK.id -> {
                                            ApksExtractor.ExtractorOption.UNIVERSAL_APK
                                        }

                                        else -> {
                                            ApksExtractor.ExtractorOption.APKS
                                        }
                                    }
                                }
                            }
                        }

                        is PartData.FileItem -> {
                            when (part.name) {
                                "keystore" -> {

                                    val fileName = part.originalFileName as String
                                    val fileExtension = ".${fileName.substringAfterLast(".")}"

                                    if (fileExtension != ".jks" && fileExtension != ".keystore") {
                                        call.sendErrorResponse(
                                            httpStatusCode = HttpStatusCode.UnsupportedMediaType,
                                            errorResponseType = ErrorResponseType.UPLOAD_INVALID_KEYSTORE_EXTENSION
                                        )
                                        return@forEachPart
                                    }

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
                                    val fileExtension = ".${fileName.substringAfterLast(".")}"

                                    if (fileExtension != ".aab") {
                                        call.sendErrorResponse(
                                            httpStatusCode = HttpStatusCode.UnsupportedMediaType,
                                            errorResponseType = ErrorResponseType.UPLOAD_INVALID_AAB_EXTENSION
                                        )
                                        return@forEachPart
                                    }

                                    val fileBytes = part.provider().readRemaining().readByteArray()

                                    viewModel.extract(
                                        fileName = fileName,
                                        fileBytes = fileBytes,
                                        keystoreInfoDto = keystoreInfoDto,
                                        extractorOption = extractorOption
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
                                                    call.sendErrorResponse(
                                                        errorResponseType = ErrorResponseType.UNKNOWN
                                                    )
                                                }
                                            }

                                            is Resource.Loading -> {}
                                        }
                                    }
                                }
                            }
                        }

                        else -> { }
                    }
                    part.dispose()
                }
            }
        }

        get("/download/{hash}") {
            val hash = call.parameters["hash"] ?: return@get call.sendErrorResponse(
                httpStatusCode = HttpStatusCode.BadRequest,
                errorResponseType = ErrorResponseType.DOWNLOAD_INVALID_FILE_NAME
            )

            val basePathDto = viewModel.getBasePathByName(hash) ?: return@get call.sendErrorResponse(
                httpStatusCode = HttpStatusCode.NotFound,
                errorResponseType = ErrorResponseType.DOWNLOAD_NOT_FOUND
            )

            val extractedFileDto =
                viewModel.getExtractedByBasePath(basePathDto) ?: return@get call.sendErrorResponse(
                    httpStatusCode = HttpStatusCode.NotFound,
                    errorResponseType = ErrorResponseType.DOWNLOAD_NOT_FOUND
                )

            val file = File(extractedFileDto.path)
            if (!file.exists()) return@get call.sendErrorResponse(
                httpStatusCode = HttpStatusCode.NotFound,
                errorResponseType = ErrorResponseType.DOWNLOAD_NOT_FOUND
            )

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName, extractedFileDto.name
                ).toString()
            )
            call.respondFile(file)
        }
    }
}