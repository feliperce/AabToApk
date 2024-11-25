package io.github.feliperce.aabtoapk.repository

import io.github.feliperce.aabtoapk.data.dto.BasePathDto
import io.github.feliperce.aabtoapk.data.dto.ExtractedFilesDto
import io.github.feliperce.aabtoapk.data.dto.UploadedFilesDto
import io.github.feliperce.aabtoapk.data.local.dao.BasePathDao
import io.github.feliperce.aabtoapk.data.local.dao.ExtractedFilesDao
import io.github.feliperce.aabtoapk.data.local.dao.UploadFilesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoveCacheRepository(
    private val basePathDao: BasePathDao,
    private val uploadFilesDao: UploadFilesDao,
    private val extractedFilesDao: ExtractedFilesDao
) {

    suspend fun getAllBasePathByDateToRemove(): List<BasePathDto> =
        withContext(Dispatchers.IO) {
            basePathDao.getAllByDateToRemove()
        }

    suspend fun getExtractedFilesByBasePath(basePathDto: BasePathDto): ExtractedFilesDto? =
        withContext(Dispatchers.IO) {
            extractedFilesDao.getByBasePath(basePathDto)
        }

    suspend fun getUploadedFilesByBasePath(basePathDto: BasePathDto): UploadedFilesDto? =
        withContext(Dispatchers.IO) {
            uploadFilesDao.getByBasePath(basePathDto)
        }

    suspend fun removeBasePathById(id: Int): Int =
        withContext(Dispatchers.IO) {
            basePathDao.removeById(id)
        }

    suspend fun removeExtractedFilesByBasePathId(basePathId: Int): Int =
        withContext(Dispatchers.IO) {
            extractedFilesDao.removeByBasePathId(basePathId)
        }

    suspend fun removeUploadedFilesByBasePathId(basePathId: Int): Int =
        withContext(Dispatchers.IO) {
            uploadFilesDao.removeByBasePathId(basePathId)
        }
}