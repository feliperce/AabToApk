package br.com.mobileti.aabtoapk

import android.app.Application
import di.initKoin
import org.koin.android.ext.koin.androidContext
import shared.di.PlatformModule

class DefaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin(
            actualModules = listOf(PlatformModule().module)
        ) {
            androidContext(this@DefaultApplication)
        }
    }
}