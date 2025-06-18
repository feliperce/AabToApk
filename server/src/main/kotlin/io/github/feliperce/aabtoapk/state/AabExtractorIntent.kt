package io.github.feliperce.aabtoapk.state

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.KeystoreInfoDto
import io.github.feliperce.aabtoapk.utils.extractor.ApksExtractor

sealed class AabExtractorIntent {
    class Extract(
        val fileName: String,
        val fileBytes: ByteArray,
        val keystoreInfoDto: KeystoreInfoDto?,
        val extractorOption: ApksExtractor.ExtractorOption
    ) : AabExtractorIntent()

    class GetBasePathByName(val name: String) : AabExtractorIntent()

    class GetExtractedByBasePath(val basePathDto: BasePathDto) : AabExtractorIntent()
}
