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
                TopAppBar(
                    title = {
                        Text(text = it.title)
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

        bottomNavigationItems.forEach { bottomNavigationItem ->
            NavigationBarItem(
                label = {
                    Text(
                        text = bottomNavigationItem.label
                    )
                },
                icon = {
                    Icon(
                        bottomNavigationItem.icon,
                        contentDescription = bottomNavigationItem.iconContentDescription
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
