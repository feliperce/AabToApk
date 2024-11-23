package io.github.feliperce.aabtoapk.data.local

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

class ExtractorDb(
    private val database: Database
) {

    object UploadedFiles : IntIdTable() {
        val name = varchar("name", length = Int.MAX_VALUE)
        val path = varchar("path", length = Int.MAX_VALUE)
        val uploadDate = datetime("upload_date")
    }

    object ExtractedFiles : IntIdTable() {
        val name = varchar("name", length = Int.MAX_VALUE)
        val path = varchar("path", length = Int.MAX_VALUE)
        val fileType = varchar("file_type", length = 4)
        val isDebugKeystore = bool("is_debug_keystore")
        val extractedDate = date("extracted_date")
        val downloadUrl = varchar("download_url", length = Int.MAX_VALUE)
        val aabFile = reference("aab_file", UploadedFiles)
    }

    init {
        transaction(database) {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(UploadedFiles, ExtractedFiles)
        }
    }

}