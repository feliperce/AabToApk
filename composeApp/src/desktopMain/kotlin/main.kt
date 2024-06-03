import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import feature.home.di.homeModule
import feature.home.view.HomeScreen
import org.koin.core.context.startKoin

fun main() = application {
    startKoin {
        modules(homeModule)
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
