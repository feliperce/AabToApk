package feature.nav.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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

@Composable
fun NavScreen() {
    val navViewModel: NavViewModel = koinViewModel<NavViewModel>()
    val navController = rememberNavController()

    val navUiState by navViewModel.navState.collectAsState()
    var currentScreen: Screen? by remember { mutableStateOf(null) }
    var startDestination = ""

    LaunchedEffect(Unit) {
        navViewModel.sendIntent(
            NavIntent.GetIsFirstAccess
        )
    }

    LaunchedEffect(navUiState.isFirstAccess) {
        startDestination = if (navUiState.isFirstAccess) {
            Screen.SettingsScreen.route
        } else {
            Screen.ExtractorScreen.route
        }
    }

    Scaffold(
        topBar = {
            currentScreen?.let {
                TopAppBar(
                    title = {
                        Text(text = it.title)
                    }
                )
            }
        },
        bottomBar = {
            if (!navUiState.isFirstAccess) {
                DefaultBottomNavigation(navController)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(navController, startDestination = startDestination) {
                composable(route = Screen.SettingsScreen.route) {
                    SettingsScreen(navController)
                    currentScreen = Screen.SettingsScreen
                }
                composable(route = Screen.ExtractorScreen.route) {
                    ExtractorScreen()
                    currentScreen = Screen.ExtractorScreen
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
                icon = {
                    Icon(
                        bottomNavigationItem.icon,
                        contentDescription = bottomNavigationItem.iconContentDescription
                    )
                },
                selected = currentRoute == bottomNavigationItem.route,
                onClick = {
                    navController.navigate(bottomNavigationItem.route) {
                        //popUpTo(bottomNavigationItem.route)
                        //launchSingleTop = true
                    }
                }
            )
        }
    }
}