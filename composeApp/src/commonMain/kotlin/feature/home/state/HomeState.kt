package feature.home.state

import feature.home.model.ExtractorFormData

data class HomeUiState (
    val loading: Boolean = false,
    var errorMsg: String? = null,
    var extractorFormData: ExtractorFormData = ExtractorFormData()
)

sealed class HomeIntent {
    class ExtractAab(val extractorFormData: ExtractorFormData) : HomeIntent()
}