package feature.home.state

import feature.home.model.ExtractorFormData

data class HomeUiState (
    val loading: Boolean = false,
    var errorMsg: String? = null,
    var extractorFormData: ExtractorFormData? = null
)

sealed class HomeIntent {
    class ExtractAab() : HomeIntent()
}