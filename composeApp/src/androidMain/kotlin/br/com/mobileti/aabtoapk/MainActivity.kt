package br.com.mobileti.aabtoapk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import feature.nav.view.NavScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestStoragePermissions {
            Log.d("MainActivity", "STORAGE PERMISSION OK")
        }

        setContent {
            NavScreen()
        }
    }
}