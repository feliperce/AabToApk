import aabtoapk.composeapp.generated.resources.Res
import aabtoapk.composeapp.generated.resources.ic_aabtoapk
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import di.initKoin
import feature.nav.view.NavScreen
import org.jetbrains.compose.resources.painterResource
import shared.di.DesktopDispatcher
import shared.di.PlatformModule

fun main() = application {
    DesktopDispatcher.initialize()
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
            state = state,
            icon = painterResource(Res.drawable.ic_aabtoapk)
        ) {
            NavScreen()
        }
    }
}
