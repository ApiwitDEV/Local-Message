package com.example.data.repository

import android.util.Log
import com.example.data.datasource.network.local.model.test.ReceivedData
import com.example.data.datasource.network.local.server.LocalServer
import com.example.data.datasource.ondevice.database.chathistory.ChatDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerRepository @Inject constructor(
    private val localServer: LocalServer,
    private val chatDao: ChatDao
) {

    fun initializeServer() {
        CoroutineScope(Dispatchers.IO).launch {
            localServer.server.collectLatest {
                Log.d("server receive", it.toString())
            }
        }
    }

    fun subscribeMessageFromOther(): SharedFlow<ReceivedData> {
        return localServer.server
    }

}