package shared.di

import feature.settings.viewmodel.SettingsViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual class PlatformModule {
    actual val module: Module = module {
        single { SettingsViewModel() }
    }
}