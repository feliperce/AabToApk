package io.github.feliperce.aabtoapk.data.local

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction

class ExtractorDb(
    private val database: Database
) {

    object BasePaths : IntIdTable() {
        val name = varchar("name", length = 200)
        val path = varchar("path", length = 1000)
        val createdDate = timestamp("upload_date")
        val dateToRemove = timestamp("date_to_remove")
    }

    object UploadedFiles : IntIdTable() {
        val basePath = reference("base_path", BasePaths).uniqueIndex()
        val name = varchar("name", length = 200)
        val path = varchar("path", length = 1000)
        val formattedName = varchar("formatted_name", length = 300).uniqueIndex()
        val uploadDate = timestamp("upload_date")
    }

    object ExtractedFiles : IntIdTable() {
        val basePath = reference("base_path", BasePaths).uniqueIndex()
        val aabFile = reference("aab_file", UploadedFiles).uniqueIndex()
        val name = varchar("name", length = 200)
        val path = varchar("path", length = 1000)
        val formattedName = varchar("formatted_name", length = 300).uniqueIndex()
        val fileExtension = varchar("file_extension", length = 5)
        val isDebugKeystore = bool("is_debug_keystore")
        val extractedDate = timestamp("extracted_date")
        val downloadUrl = varchar("download_url", length = 200)
    }

    init {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(BasePaths, UploadedFiles, ExtractedFiles)
        }
    }

}