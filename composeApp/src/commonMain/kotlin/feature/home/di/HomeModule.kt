package feature.home.di

import feature.home.viewmodel.HomeViewModel
import org.koin.dsl.module

val homeModule = module {
    single { HomeViewModel() }
}