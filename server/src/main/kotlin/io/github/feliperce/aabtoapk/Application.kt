package io.github.feliperce.aabtoapk

import io.github.feliperce.aabtoapk.data.remote.ServerConstants
import io.github.feliperce.aabtoapk.data.remote.response.AabConvertResponse
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.io.File
import java.util.*

fun main() {
    ServerConstants.PathConf.mkdirs()

    embeddedServer(Netty, port = ServerConstants.PORT, host = ServerConstants.HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {

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

            val multipartData = call.receiveMultipart(formFieldLimit = Long.MAX_VALUE)

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }

                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.provider().readRemaining().readByteArray()

                        val uploadDir = File(ServerConstants.PathConf.CACHE_PATH)

                        val cachedAab = File("${uploadDir.absolutePath}/$fileName")
                        cachedAab.writeBytes(fileBytes)

                        println("FILE CREATED -> $fileName")

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

                            }
                        )

                        println("SET KEYSTORE")
                        extractor.aabToApks(
                            extractorOption = ApksExtractor.ExtractorOption.APKS,
                            onSuccess =  { path, name ->
                                resultPath = path
                                fileName = "${name}.apks"
                            },
                            onFailure = {

                            }
                        )

                        println("extracted")
                    }

                    else -> {}
                }
                part.dispose()
            }

            call.respond(
                AabConvertResponse(
                    fileName = fileName,
                    fileType = "apks",
                    downloadUrl = "da",
                    date = Date().time,
                    debugKeystore = true
                )
            )
        }
    }
}