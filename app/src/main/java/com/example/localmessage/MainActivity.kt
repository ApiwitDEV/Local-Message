package com.example.localmessage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.data.repository.NSDRepository
import com.example.data.repository.ServerRepository
import com.example.localmessage.feature.ui.MainScreen
import com.example.localmessage.ui.theme.LocalMessageTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val serverRepository by inject<ServerRepository>()
    private val nsdRepository by inject<NSDRepository>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverRepository.initializeServer()
        nsdRepository.initializeNSD()
        enableEdgeToEdge()
        setContent {
            LocalMessageTheme {
                MainScreen()
            }
        }
    }
}