package feature.nav.state

data class NavUiState(
    val isFirstAccess: Boolean = true
)

sealed class NavIntent {
    data object GetIsFirstAccess: NavIntent()
}