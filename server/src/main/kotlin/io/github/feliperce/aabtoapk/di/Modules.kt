package io.github.feliperce.aabtoapk.di

import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import io.github.feliperce.aabtoapk.data.local.dao.ExtractorDao
import io.github.feliperce.aabtoapk.repository.AabExtractorRepository
import io.github.feliperce.aabtoapk.viewmodel.AabExtractorViewModel
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val dataModule = module {
    single {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
    }

    single { ExtractorDb(get()) }
    single { ExtractorDao(get()) }
}

val extractorModule = module {
    single {
        AabExtractorRepository(get())
    }

    factory {
        AabExtractorViewModel(get())
    }
}