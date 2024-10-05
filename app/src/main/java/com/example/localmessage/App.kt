package com.example.localmessage

import android.app.Application
import android.util.Log
import com.example.data.dataModule
import com.example.data.repository.NSDRepository
import com.example.data.repository.ServerRepository
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {

    private val tag = "App"

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate")
        val allModule = listOf(appModule, dataModule)
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@App)
            // Load modules
            modules(allModule)
        }
        val nsdRepository by inject<NSDRepository>()
        val serverRepository by inject<ServerRepository>()

        nsdRepository.initializeNSD()
        serverRepository.initializeServer()
    }

}