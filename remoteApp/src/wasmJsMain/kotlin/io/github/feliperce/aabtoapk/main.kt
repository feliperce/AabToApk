package io.github.feliperce.aabtoapk

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.feliperce.aabtoapk.feature.extractor.data.di.dataModule
import io.github.feliperce.aabtoapk.feature.extractor.di.extractorModule
import io.github.feliperce.aabtoapk.feature.extractor.view.ExtractorScreen
import kotlinx.browser.document
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(
            listOf(dataModule, extractorModule)
        )
    }

    ComposeViewport(document.body!!) {
        ExtractorScreen()
    }
}