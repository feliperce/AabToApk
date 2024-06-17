package shared.di

import feature.extractor.viewmodel.ExtractorViewModel
import feature.settings.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual class PlatformModule {
    actual val module: Module = module {
        viewModel { ExtractorViewModel() }
        viewModel { SettingsViewModel() }
    }
}