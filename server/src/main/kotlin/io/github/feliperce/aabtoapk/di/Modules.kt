package io.github.feliperce.aabtoapk.di

import io.github.feliperce.aabtoapk.config.ServerConfig
import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.dao.BasePathDao
import io.github.feliperce.aabtoapk.data.local.dao.ExtractedFilesDao
import io.github.feliperce.aabtoapk.data.local.dao.UploadFilesDao
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.repository.RemoveCacheRepository
import io.github.feliperce.aabtoapk.viewmodel.AabExtractorViewModel
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val dataModule = module {
    single {
        Database.connect(
            ServerConfig.Database.URL,
            user = ServerConfig.Database.USER,
            password = ServerConfig.Database.PASSWORD
        )
    }

    single(createdAtStart = true) { ExtractorDb(get()) }
    single { BasePathDao() }
    single { UploadFilesDao() }
    single { ExtractedFilesDao() }
}

val extractorModule = module {
    single {
        AabExtractorRepository(get(), get(), get())
    }

    single { RemoveCacheRepository(get(), get(), get()) }

    factory {
        AabExtractorViewModel(get())
    }
}
