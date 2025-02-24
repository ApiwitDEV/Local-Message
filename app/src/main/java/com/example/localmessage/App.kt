package com.example.localmessage

import android.app.Application
import android.util.Log
import com.example.data.repository.NSDRepository
import com.example.data.repository.ServerRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

    private val tag = "App"

    @Inject lateinit var nsdRepository: NSDRepository
    @Inject lateinit var serverRepository: ServerRepository

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate")
//        val allModule = listOf(appModule, dataModule)
//        startKoin {
//            // Log Koin into Android logger
//            androidLogger()
//            // Reference Android context
//            androidContext(this@App)
//            // Load modules
//            modules(allModule)
//        }
//
        nsdRepository.initializeNSD()
        serverRepository.initializeServer()
    }

}