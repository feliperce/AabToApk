import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import di.initKoin
import feature.nav.view.NavScreen
import shared.di.PlatformModule

fun main() = application {
    initKoin(
        actualModules = listOf(PlatformModule().module)
    )

    MaterialTheme {
        val state = rememberWindowState(
            placement = WindowPlacement.Maximized,
            position = WindowPosition(Alignment.Center)
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = "AabToApk",
            state = state
        ) {
            NavScreen()
        }
    }
}
