package shared.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import data.local.db.ExtractorDatabase
import shared.settings.AppSettings
import shared.settings.createDataStore

fun dataStore(context: Context): DataStore<Preferences> =
    createDataStore(
        producePath = { context.filesDir.resolve(AppSettings.DATA_STORE_FILE).absolutePath }
    )

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<ExtractorDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(ExtractorDatabase.DATABASE_NAME)
    return Room.databaseBuilder<ExtractorDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}