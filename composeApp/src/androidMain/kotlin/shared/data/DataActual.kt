package shared.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import shared.settings.AppSettings
import shared.settings.createDataStore

fun dataStore(context: Context): DataStore<Preferences> =
    createDataStore(
        producePath = { context.filesDir.resolve(AppSettings.DATA_STORE_FILE).absolutePath }
    )