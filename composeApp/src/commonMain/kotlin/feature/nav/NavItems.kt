package feature.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val title: String) {
    data object ExtractorScreen : Screen("ExtractorScreen")
    data object SettingsScreen : Screen("SettingsScreen")
}

data class BottomNavigationItem(
    val route: String,
    val icon: ImageVector,
    val iconContentDescription: String
)

val bottomNavigationItems = listOf(
    BottomNavigationItem(
        Screen.ExtractorScreen.title,
        Icons.Default.Person,
        "Extractor"
    ),
    BottomNavigationItem(
        Screen.SettingsScreen.title,
        Icons.Filled.DateRange,
        "Settings"
    )
)
