package br.com.mobileti.aabtoapk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import di.initKoin
import feature.extractor.view.ExtractorScreen
import org.koin.core.context.startKoin
import shared.di.PlatformModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin {
            modules(PlatformModule().module)
        }
        startKoin {

        }

        requestStoragePermissions {
            Log.d("MainActivity", "STORAGE PERMISSION OK")
        }

        setContent {
            ExtractorScreen()
        }
    }
}