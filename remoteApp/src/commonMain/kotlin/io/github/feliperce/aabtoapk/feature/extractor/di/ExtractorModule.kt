package io.github.feliperce.aabtoapk.feature.extractor.di

import io.github.feliperce.aabtoapk.feature.extractor.repository.ExtractorRepository
import io.github.feliperce.aabtoapk.feature.extractor.viewmodel.ExtractorViewModel
import org.koin.dsl.module

val extractorModule = module {

    single { ExtractorRepository(get()) }
    factory {  ExtractorViewModel(get()) }


}