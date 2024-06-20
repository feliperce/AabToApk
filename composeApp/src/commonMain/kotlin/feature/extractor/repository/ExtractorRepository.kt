package feature.extractor.repository

import data.local.dao.ExtractorDao
import feature.extractor.mapper.KeystoreDto
import feature.extractor.mapper.toKeystoreDtoList
import feature.extractor.mapper.toKeystoreEntity
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ExtractorRepository(
    private val extractorDao: ExtractorDao
) {

    suspend fun insertOrUpdateKeystore(
        keystoreDto: KeystoreDto
    ) {
        extractorDao.insertOrUpdate(keystoreDto.toKeystoreEntity())
    }

    suspend fun getKeystoreAll() = flow<List<KeystoreDto>> {
        emitAll(
            extractorDao.getAll().map {
                it.toKeystoreDtoList()
            }
        )
    }

    suspend fun deleteKeystore(keystoreDto: KeystoreDto) {
        extractorDao.delete(keystoreDto.toKeystoreEntity())
    }

}