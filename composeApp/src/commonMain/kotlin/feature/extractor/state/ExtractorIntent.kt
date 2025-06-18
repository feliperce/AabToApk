package feature.extractor.state

import feature.extractor.mapper.KeystoreDto
import feature.extractor.model.ExtractorFormData
import ui.components.RadioItem

sealed class ExtractorIntent {
    class ExtractAab(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class InstallApks(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class InstallApk(val extractorFormData: ExtractorFormData) : ExtractorIntent()
    class SaveKeystore(val keystoreDto: KeystoreDto) : ExtractorIntent()
    class RemoveKeystore(val keystoreDto: KeystoreDto) : ExtractorIntent()
    class SetShowKeystoreRemoveDialog(val show: Boolean) : ExtractorIntent()
    class SetShowErrorDialog(val show: Boolean) : ExtractorIntent()
    class UpdateAabPath(val path: String) : ExtractorIntent()
    class UpdateKeystoreDto(val keystoreDto: KeystoreDto) : ExtractorIntent()
    class UpdateExtractOptions(val options: List<RadioItem>) : ExtractorIntent()
    class UpdateSelectedExtractOption(val option: RadioItem) : ExtractorIntent()
    data object GetSettingsData : ExtractorIntent()
    data object GetKeystoreData : ExtractorIntent()
}
