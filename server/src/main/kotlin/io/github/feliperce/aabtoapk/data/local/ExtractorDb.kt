package io.github.feliperce.aabtoapk.data.local

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

class ExtractorDb(
    private val database: Database
) {

    object BasePaths : IntIdTable() {
        val name = varchar("name", length = 200)
        val createdDate = datetime("upload_date")
        val dateToRemove = datetime("date_to_remove")
    }

    object UploadedFiles : IntIdTable() {
        val basePath = reference("base_path", BasePaths).uniqueIndex()
        val name = varchar("name", length = 200)
        val path = varchar("path", length = 2000)
        val hash = varchar("hash", length = 300).uniqueIndex()
        val formattedName = varchar("formatted_name", length = 300).uniqueIndex()
        val uploadDate = datetime("upload_date")
    }

    object ExtractedFiles : IntIdTable() {
        val basePath = reference("base_path", BasePaths).uniqueIndex()
        val aabFile = reference("aab_file", UploadedFiles).uniqueIndex()
        val name = varchar("name", length = 200)
        val path = varchar("path", length = 2000)
        val formattedName = varchar("formatted_name", length = 300).uniqueIndex()
        val hash = varchar("hash", length = 300).uniqueIndex()
        val fileExtension = varchar("file_extension", length = 5)
        val isDebugKeystore = bool("is_debug_keystore")
        val extractedDate = datetime("extracted_date")
        val downloadUrl = varchar("download_url", length = 200)
    }

    init {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UploadedFiles, ExtractedFiles)
        }
    }

}