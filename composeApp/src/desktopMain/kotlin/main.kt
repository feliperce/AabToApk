import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import shared.di.PlatformModule
import feature.extractor.view.HomeScreen
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        val platformModule = PlatformModule()

        modules(platformModule.module)
    }

    MaterialTheme {
        val state = rememberWindowState(
            placement = WindowPlacement.Maximized,
            position = WindowPosition(Alignment.Center)
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = "AarToApk",
            state = state
        ) {
            HomeScreen()
        }
    }
}
