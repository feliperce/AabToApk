package io.github.feliperce.aabtoapk.feature.extractor.state

import io.github.feliperce.aabtoapk.feature.extractor.model.AabFileDto
import io.github.feliperce.aabtoapk.feature.extractor.model.ExtractorOption
import io.github.feliperce.aabtoapk.feature.extractor.model.KeystoreDto
import io.github.vinceglb.filekit.core.PlatformFile

sealed class ExtractorIntent {
    class UploadAndExtract(val keystoreDto: KeystoreDto, val aabFileDto: AabFileDto) : ExtractorIntent()
    class UpdateKeystoreDebug(val isDebugKeystore: Boolean) : ExtractorIntent()
    class UpdateExtractorOption(val extractorOption: ExtractorOption) : ExtractorIntent()
    class UpdateAabFile(val file: PlatformFile) : ExtractorIntent()
    class UpdateKeystoreFile(val file: PlatformFile) : ExtractorIntent()
    class UpdateKeystorePassword(val password: String) : ExtractorIntent()
    class UpdateKeystoreAlias(val alias: String) : ExtractorIntent()
    class UpdateKeystoreKeyPassword(val keyPassword: String) : ExtractorIntent()
    data object ResetExtractorResponse : ExtractorIntent()
}
