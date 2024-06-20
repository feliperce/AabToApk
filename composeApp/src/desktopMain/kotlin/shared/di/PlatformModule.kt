package shared.di

import data.local.db.ExtractorDatabase
import data.local.db.getRoomDatabase
import feature.extractor.viewmodel.ExtractorViewModel
import feature.settings.viewmodel.SettingsViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import shared.data.dataStore
import shared.data.getDatabaseBuilder

actual class PlatformModule {
    actual val module: Module = module {
        single { dataStore() }
        single { getRoomDatabase(getDatabaseBuilder()) }

        single {
            val db = get<ExtractorDatabase>()
            db.extractorDao()
        }

        factory { ExtractorViewModel(get(), get()) }
        factory { SettingsViewModel(get()) }
    }
}