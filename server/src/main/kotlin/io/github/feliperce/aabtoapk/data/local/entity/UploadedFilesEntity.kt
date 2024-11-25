package io.github.feliperce.aabtoapk.data.local.entity

import io.github.feliperce.aabtoapk.data.local.ExtractorDb
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UploadedFilesEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UploadedFilesEntity>(ExtractorDb.UploadedFiles)

    var basePath by BasePathEntity referencedOn ExtractorDb.UploadedFiles.basePath
    var name by ExtractorDb.UploadedFiles.name
    var path by ExtractorDb.UploadedFiles.path
    var formattedName by ExtractorDb.UploadedFiles.formattedName
    var uploadedDate by ExtractorDb.UploadedFiles.uploadDate
}
