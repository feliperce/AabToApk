import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import di.initKoin
import shared.di.PlatformModule
import feature.settings.view.SettingsScreen
import org.koin.core.context.startKoin

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
            SettingsScreen()
            //HomeScreen()
        }
    }
}
