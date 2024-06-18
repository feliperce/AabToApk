package feature.extractor.state

import feature.extractor.model.ExtractorFormData
import utils.ErrorMsg
import utils.SuccessMsg

data class ExtractorUiState (
    val loading: Boolean = false,
    val errorMsg: ErrorMsg = ErrorMsg(),
    val successMsg: SuccessMsg = SuccessMsg(),
    val extractedApksPath: String = ""
)

sealed class ExtractorIntent {
    class ExtractAab(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class InstallApks(val extractorFormData: ExtractorFormData) : ExtractorIntent()
}