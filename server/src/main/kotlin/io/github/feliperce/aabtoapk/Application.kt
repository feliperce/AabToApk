package io.github.feliperce.aabtoapk

import io.github.feliperce.aabtoapk.data.remote.ServerConstants
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

fun main() {
    File(ServerConstants.CACHE_PATH).mkdirs()
    File(ServerConstants.EXTRACTOR_PATH).mkdirs()

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

            val multipartData = call.receiveMultipart(formFieldLimit = Long.MAX_VALUE)

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }

                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.provider().readRemaining().readByteArray()

                        val uploadDir = File(ServerConstants.CACHE_PATH)

                        val cachedAab = File("${uploadDir.path}$fileName")
                        cachedAab.writeBytes(fileBytes)
                    }

                    else -> {}
                }
                part.dispose()
            }

            call.respondText("$fileDescription is uploaded to 'uploads/$fileName'")
        }
    }
}