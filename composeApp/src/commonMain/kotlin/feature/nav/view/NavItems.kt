package feature.nav.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val title: String, val route: String) {
    data object ExtractorScreen : Screen("AarToApk", "extractor")
    data object SettingsScreen : Screen("Settings", "settings")
}

data class BottomNavigationItem(
    val route: String,
    val icon: ImageVector,
    val iconContentDescription: String
)

val bottomNavigationItems = listOf(
    BottomNavigationItem(
        Screen.ExtractorScreen.route,
        Icons.Default.Person,
        "Extractor"
    ),
    BottomNavigationItem(
        Screen.SettingsScreen.route,
        Icons.Filled.DateRange,
        "Settings"
    )
)
