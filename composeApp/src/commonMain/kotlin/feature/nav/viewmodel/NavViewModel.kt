package feature.nav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feature.nav.state.NavIntent
import feature.nav.state.NavUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NavViewModel : ViewModel() {

    private val intentChannel = Channel<NavIntent>(Channel.UNLIMITED)
    
    private val _navState = MutableStateFlow(NavUiState())
    val navState: StateFlow<NavUiState> = _navState.asStateFlow()
    
    init {
        handleIntents()
    }
    
    fun sendIntent(intent: NavIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }
    
    private fun handleIntents() {
        intentChannel
            .consumeAsFlow()
            .onEach { intent ->
                when(intent) {
                    is NavIntent.SetCurrentScreen -> {
                        updateState { it.copy(currentScreen = intent.screen) }
                    }
                }
            }.launchIn(viewModelScope)
    }
    
    private fun reduce(currentState: NavUiState, newState: NavUiState): NavUiState {
        return newState
    }
    
    private fun updateState(stateReducer: (NavUiState) -> NavUiState) {
        _navState.update { currentState ->
            reduce(currentState, stateReducer(currentState))
        }
    }
}