package feature.nav.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import aabtoapk.composeapp.generated.resources.Res
import aabtoapk.composeapp.generated.resources.extractor_title
import aabtoapk.composeapp.generated.resources.settings_title
import aabtoapk.composeapp.generated.resources.extractor_label
import aabtoapk.composeapp.generated.resources.settings_label
import org.jetbrains.compose.resources.stringResource

sealed class Screen(val title: String, val route: String) {
    data object ExtractorScreen : Screen("AabToApk", "extractor")
    data object SettingsScreen : Screen("Settings", "settings")
}

data class BottomNavigationItem(
    val route: String,
    val icon: ImageVector,
    val iconContentDescription: String,
    val label: String
)

val bottomNavigationItems = listOf(
    BottomNavigationItem(
        route = Screen.ExtractorScreen.route,
        icon = Icons.Default.SwapHoriz,
        iconContentDescription = "Extractor",
        label = "Extractor"
    ),
    BottomNavigationItem(
        route = Screen.SettingsScreen.route,
        icon = Icons.Filled.Settings,
        iconContentDescription = "Settings",
        label = "Settings"
    )
)
