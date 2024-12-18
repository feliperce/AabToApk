package io.github.feliperce.aabtoapk.data.local.entity

import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ExtractedFileEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ExtractedFileEntity>(ExtractorDb.ExtractedFiles)

    var aabFile by UploadedFilesEntity referencedOn ExtractorDb.ExtractedFiles.aabFile
    var basePath by BasePathEntity referencedOn ExtractorDb.ExtractedFiles.basePath
    var name by ExtractorDb.ExtractedFiles.name
    var path by ExtractorDb.ExtractedFiles.path
    var fileType by ExtractorDb.ExtractedFiles.fileExtension
    var formattedName by ExtractorDb.ExtractedFiles.formattedName
    var isDebugKeystore by ExtractorDb.ExtractedFiles.isDebugKeystore
    var extractedDate by ExtractorDb.ExtractedFiles.extractedDate
    var downloadUrl by ExtractorDb.ExtractedFiles.downloadUrl
}
