package feature.extractor.state

import feature.extractor.mapper.KeystoreDto
import feature.extractor.model.ExtractorFormData
import shared.settings.SettingsData
import io.github.feliperce.aabtoapk.utils.extractor.ErrorMsg
import io.github.feliperce.aabtoapk.utils.extractor.SuccessMsg

data class ExtractorUiState (
    val loading: Boolean = false,
    val errorMsg: ErrorMsg = ErrorMsg(),
    val successMsg: SuccessMsg = SuccessMsg(),
    val extractedApksPath: String = "",
    val settingsData: SettingsData? = null,
    val keystoreDtoList: List<KeystoreDto> = listOf()
)

sealed class ExtractorIntent {
    class ExtractAab(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class InstallApks(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class InstallApk(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class SaveKeystore(val keystoreDto: KeystoreDto) : ExtractorIntent()
    class RemoveKeystore(val keystoreDto: KeystoreDto) : ExtractorIntent()
    data object GetSettingsData : ExtractorIntent()
    data object GetKeystoreData : ExtractorIntent()
}