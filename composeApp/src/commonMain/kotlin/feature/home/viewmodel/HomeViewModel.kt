package feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import feature.home.model.ExtractorFormData
import feature.home.state.HomeIntent
import feature.home.state.HomeUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val intentChannel = Channel<HomeIntent>(Channel.UNLIMITED)

    private val _homeState = MutableStateFlow(HomeUiState(loading = false))
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: HomeIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        intentChannel
            .consumeAsFlow()
            .onEach { intent ->
                when(intent) {
                    is HomeIntent.ExtractAab -> {
                        extractAab(intent.extractorFormData)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun extractAab(extractorFormData: ExtractorFormData) {
        viewModelScope.launch {

        }
    }

}