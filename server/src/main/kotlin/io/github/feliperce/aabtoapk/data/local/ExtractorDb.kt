package io.github.feliperce.aabtoapk.data.local

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

class ExtractorDb(
    private val database: Database
) {

    object UploadedFiles : IntIdTable() {
        val name = varchar("name", length = 200)
        val path = varchar("path", length = 2000)
        val uploadDate = datetime("upload_date")
    }

    object ExtractedFiles : IntIdTable() {
        val name = varchar("name", length = 200)
        val path = varchar("path", length = 2000)
        val fileType = varchar("file_type", length = 4)
        val isDebugKeystore = bool("is_debug_keystore")
        val extractedDate = datetime("extracted_date")
        val downloadUrl = varchar("download_url", length = 200)
        val aabFile = reference("aab_file", UploadedFiles).uniqueIndex()
    }

    init {
        transaction(database) {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(UploadedFiles, ExtractedFiles)
        }
    }

}