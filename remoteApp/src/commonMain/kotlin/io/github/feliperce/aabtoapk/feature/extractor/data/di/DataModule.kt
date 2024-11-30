package io.github.feliperce.aabtoapk.feature.extractor.data.di

import io.github.feliperce.aabtoapk.feature.extractor.data.remote.ExtractorApi
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    single {
        HttpClient(Js) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            defaultRequest {
                header("Content-Type", ContentType.Application.Json)
            }
            expectSuccess = true
        }
    }

    single { ExtractorApi(get()) }
}