package di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import shared.settings.AppSettings

fun initKoin(appDeclarations: KoinAppDeclaration = {}) =
    startKoin {
        appDeclarations()
        modules(dataModule)
    }

internal val dataModule = module {

    single { AppSettings(get()) }
}