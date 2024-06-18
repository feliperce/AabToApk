package shared.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import shared.settings.AppSettings
import shared.settings.createDataStore

fun dataStore(): DataStore<Preferences> =
    createDataStore(
        producePath = { AppSettings.DATA_STORE_FILE }
    )