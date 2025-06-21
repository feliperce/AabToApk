package feature.nav.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import feature.extractor.view.ExtractorScreen
import feature.nav.state.NavIntent
import feature.nav.viewmodel.NavViewModel
import feature.settings.view.SettingsScreen
import org.koin.compose.viewmodel.koinViewModel
import aabtoapk.composeapp.generated.resources.Res
import aabtoapk.composeapp.generated.resources.extractor_title
import aabtoapk.composeapp.generated.resources.settings_title
import aabtoapk.composeapp.generated.resources.extractor_label
import aabtoapk.composeapp.generated.resources.settings_label
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavScreen() {
    val navController = rememberNavController()
    val navViewModel: NavViewModel = koinViewModel()
    val navUiState by navViewModel.navState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            navUiState.currentScreen?.let {
                val title = when (it) {
                    Screen.ExtractorScreen -> stringResource(Res.string.extractor_title)
                    Screen.SettingsScreen -> stringResource(Res.string.settings_title)
                }
                TopAppBar(
                    title = {
                        Text(text = title)
                    }
                )
            }
        },
        bottomBar = {
            DefaultBottomNavigation(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(navController, startDestination = Screen.ExtractorScreen.route) {
                composable(route = Screen.SettingsScreen.route) {
                    SettingsScreen(
                        snackbarHostState = snackbarHostState
                    )
                    navViewModel.sendIntent(NavIntent.SetCurrentScreen(Screen.SettingsScreen))
                }
                composable(route = Screen.ExtractorScreen.route) {
                    ExtractorScreen(
                        snackbarHostState = snackbarHostState
                    )
                    navViewModel.sendIntent(NavIntent.SetCurrentScreen(Screen.ExtractorScreen))
                }
            }
        }
    }
}

@Composable
fun DefaultBottomNavigation(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val extractorLabel = stringResource(Res.string.extractor_label)
        val settingsLabel = stringResource(Res.string.settings_label)

        bottomNavigationItems.forEach { bottomNavigationItem ->
            val label = when (bottomNavigationItem.route) {
                Screen.ExtractorScreen.route -> extractorLabel
                Screen.SettingsScreen.route -> settingsLabel
                else -> bottomNavigationItem.label
            }

            val contentDescription = when (bottomNavigationItem.route) {
                Screen.ExtractorScreen.route -> extractorLabel
                Screen.SettingsScreen.route -> settingsLabel
                else -> bottomNavigationItem.iconContentDescription
            }

            NavigationBarItem(
                label = {
                    Text(
                        text = label
                    )
                },
                icon = {
                    Icon(
                        bottomNavigationItem.icon,
                        contentDescription = contentDescription
                    )
                },
                selected = currentRoute == bottomNavigationItem.route,
                onClick = {
                    navController.navigate(bottomNavigationItem.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
