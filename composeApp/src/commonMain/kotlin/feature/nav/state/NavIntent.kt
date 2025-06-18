package feature.nav.state

import feature.nav.view.Screen

sealed class NavIntent {
    class SetCurrentScreen(val screen: Screen) : NavIntent()
}