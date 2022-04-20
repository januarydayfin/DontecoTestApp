package com.krayapp.dontecotestapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DontecoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@DontecoApp)
            modules(Koin.getModules())
        }
    }
}