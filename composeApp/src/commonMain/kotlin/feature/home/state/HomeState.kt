package feature.home.state

import feature.home.model.ExtractorFormData
import utils.ErrorMsg
import utils.SuccessMsg

data class HomeUiState (
    val loading: Boolean = false,
    val errorMsg: ErrorMsg = ErrorMsg(),
    val successMsg: SuccessMsg = SuccessMsg(),
    val extractedApksPath: String = ""
)

sealed class HomeIntent {
    class ExtractAab(val extractorFormData: ExtractorFormData) : HomeIntent()
    class InstallApks(val extractorFormData: ExtractorFormData) : HomeIntent()
}