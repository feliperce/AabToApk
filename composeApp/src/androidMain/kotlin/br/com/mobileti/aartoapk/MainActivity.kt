package br.com.mobileti.aartoapk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import feature.home.di.homeModule
import feature.home.view.HomeScreen
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            modules(homeModule)
        }

        setContent {
            HomeScreen()
        }
    }
}