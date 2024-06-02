import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import feature.home.view.HomeScreen

fun main() = application {
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
