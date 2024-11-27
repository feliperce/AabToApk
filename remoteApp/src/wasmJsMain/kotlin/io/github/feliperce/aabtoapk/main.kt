package io.github.feliperce.aabtoapk

import androidx.compose.material3.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    /*initKoin {

    }*/

    ComposeViewport(document.body!!) {
        //App()
        Text("dasdasdasdasdas asd asd")
    }
}