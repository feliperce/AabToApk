package feature.extractor.di

import feature.extractor.viewmodel.ExtractorViewModel
import org.koin.dsl.module

val extractorModule = module {
    single { ExtractorViewModel() }
}