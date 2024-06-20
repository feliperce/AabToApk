package shared.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import data.local.db.ExtractorDatabase
import shared.settings.AppSettings
import shared.settings.createDataStore
import java.io.File

fun dataStore(): DataStore<Preferences> =
    createDataStore(
        producePath = { System.getProperty("java.io.tmpdir")+"/"+AppSettings.DATA_STORE_FILE }
    )

fun getDatabaseBuilder(): RoomDatabase.Builder<ExtractorDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), ExtractorDatabase.DATABASE_NAME)
    return Room.databaseBuilder<ExtractorDatabase>(
        name = dbFile.absolutePath,
    )
}