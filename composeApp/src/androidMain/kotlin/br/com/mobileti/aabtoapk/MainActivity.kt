package br.com.mobileti.aabtoapk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import di.initKoin
import feature.settings.view.SettingsScreen
import org.koin.android.ext.koin.androidContext
import shared.di.PlatformModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin(
            actualModules = listOf(PlatformModule().module)
        ) {
            androidContext(this@MainActivity)
        }

        requestStoragePermissions {
            Log.d("MainActivity", "STORAGE PERMISSION OK")
        }

        setContent {
            SettingsScreen()
        }
    }
}