package br.com.mobileti.aabtoapk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import feature.extractor.di.extractorModule
import feature.extractor.view.ExtractorScreen
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            modules(extractorModule)
        }

        requestStoragePermissions {
            Log.d("MainActivity", "STORAGE PERMISSION OK")
        }

        setContent {
            ExtractorScreen()
        }
    }
}