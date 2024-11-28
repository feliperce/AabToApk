package io.github.feliperce.aabtoapk

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.feliperce.aabtoapk.feature.extractor.view.ExtractorScreen
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    /*initKoin {

    }*/

    ComposeViewport(document.body!!) {
        //App()
        ExtractorScreen()
    }
}