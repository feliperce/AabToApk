package shared.di

import feature.extractor.viewmodel.ExtractorViewModel
import feature.settings.viewmodel.SettingsViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual class PlatformModule {
    actual val module: Module = module {
        single { ExtractorViewModel() }
        single { SettingsViewModel() }
    }
}