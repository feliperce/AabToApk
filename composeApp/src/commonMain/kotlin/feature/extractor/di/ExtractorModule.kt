package feature.extractor.di

import feature.settings.viewmodel.SettingsViewModel
import org.koin.dsl.module

val extractorModule = module {
    single { SettingsViewModel() }
}