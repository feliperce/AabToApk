package shared.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.gosyer.appdirs.AppDirs
import data.local.db.ExtractorDatabase
import shared.settings.AppSettings
import shared.settings.createDataStore
import utils.getUserDataDirPath
import java.io.File

fun dataStore(): DataStore<Preferences> =
    createDataStore(
        producePath = { getUserDataDirPath()+"/"+AppSettings.DATA_STORE_FILE }
    )

fun getDatabaseBuilder(): RoomDatabase.Builder<ExtractorDatabase> {
    val dbFile = File(getUserDataDirPath(), ExtractorDatabase.DATABASE_NAME)
    return Room.databaseBuilder<ExtractorDatabase>(
        name = dbFile.absolutePath,
    )
}