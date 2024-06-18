package feature.nav.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feature.nav.repository.NavRepository
import feature.nav.state.NavIntent
import feature.nav.state.NavUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NavViewModel(
    private val navRepository: NavRepository
) : ViewModel() {

    private val intentChannel = Channel<NavIntent>(Channel.UNLIMITED)

    private val _navState = MutableStateFlow(NavUiState(isFirstAccess = true))
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
                    is NavIntent.GetIsFirstAccess -> {
                        getIsFirstAccess()
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getIsFirstAccess() {
        viewModelScope.launch {
            navRepository.getIsFirstAccess().collect { isFirstAccess ->
                _navState.update {
                    it.copy(isFirstAccess = isFirstAccess)
                }
            }
        }
    }
}