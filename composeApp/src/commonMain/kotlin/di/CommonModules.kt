package di

import feature.nav.repository.NavRepository
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
            listOf(dataModule, settingsModule, navModule) + actualModules
        )
    }

internal val dataModule = module {
    single { AppSettings(get()) }
}

internal val settingsModule = module {
    single { SettingsRepository(get()) }
}

internal val navModule = module {
    single { NavRepository(get()) }
}