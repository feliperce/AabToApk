package di

import data.local.dao.ExtractorDao
import data.local.db.getRoomDatabase
import feature.extractor.repository.ExtractorRepository
import feature.settings.repository.SettingsRepository
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import shared.settings.AppSettings

fun initKoin(
    actualModules: List<Module> = listOf(),
    appDeclarations: KoinAppDeclaration = {}
) =
    startKoin {
        appDeclarations()
        modules(
            listOf(dataModule, settingsModule, extractorModule) + actualModules
        )
    }

internal val dataModule = module {
    single { AppSettings(get()) }
}

internal val settingsModule = module {
    single { SettingsRepository(get()) }
}

internal val extractorModule = module {
    single { ExtractorRepository(get()) }
}