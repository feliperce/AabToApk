package feature.nav.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val title: String, val route: String) {
    data object ExtractorScreen : Screen("AarToApk", "extractor")
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
