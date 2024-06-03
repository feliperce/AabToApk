package feature.home.state

import feature.home.model.ExtractorFormData
import utils.ErrorMsg
import utils.SuccessMsg

data class HomeUiState (
    val loading: Boolean = false,
    var errorMsg: ErrorMsg = ErrorMsg(),
    var extractorFormData: ExtractorFormData = ExtractorFormData(),
    val successMsg: SuccessMsg = SuccessMsg()
)

sealed class HomeIntent {
    class ExtractAab(val extractorFormData: ExtractorFormData) : HomeIntent()
}