package io.github.feliperce.aabtoapk.job

import io.github.feliperce.aabtoapk.repository.RemoveCacheRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.io.File

class RemoveCacheJob : Job, KoinComponent {

    private val removeCacheRepository by inject<RemoveCacheRepository>()
    private var scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    override fun execute(context: JobExecutionContext) {

        scope.launch {
            val basePathsToRemove = removeCacheRepository.getAllBasePathByDateToRemove()

            basePathsToRemove.forEach { basePathDto ->
                val basePathDir = File(basePathDto.path)

                removeCacheRepository.getExtractedFilesByBasePath(basePathDto)?.let { extractedFilesDto ->
                    removeCacheRepository.removeExtractedFilesByBasePathId(basePathDto.id)
                }
                removeCacheRepository.getUploadedFilesByBasePath(basePathDto)?.let { uploadedFilesDto ->
                    removeCacheRepository.removeUploadedFilesByBasePathId(basePathDto.id)
                }
                removeCacheRepository.removeBasePathById(basePathDto.id)

                basePathDir.deleteRecursively()
            }
        }

    }

}


