package shared.di

import feature.home.viewmodel.HomeViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual class PlatformModule {
    actual val module: Module = module {
        single { HomeViewModel() }
    }
}