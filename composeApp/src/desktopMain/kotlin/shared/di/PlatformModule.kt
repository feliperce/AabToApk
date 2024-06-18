package shared.di

import feature.extractor.viewmodel.ExtractorViewModel
import feature.nav.viewmodel.NavViewModel
import feature.settings.viewmodel.SettingsViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import shared.data.dataStore

actual class PlatformModule {
    actual val module: Module = module {
        single { dataStore() }

        single { ExtractorViewModel() }
        single { SettingsViewModel(get()) }
        single { NavViewModel(get()) }
    }
}