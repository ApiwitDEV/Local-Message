package com.example.data.repository

import android.util.Log
import com.example.data.datasource.network.local.model.test.ReceivedData
import com.example.data.datasource.network.local.model.test.TestRequestBody
import com.example.data.datasource.network.local.server.LocalServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ServerRepository(
    private val localServer: LocalServer
) {

    fun initializeServer() {
        CoroutineScope(Dispatchers.IO).launch {
            localServer.server.collectLatest {
                Log.d("server receive", it.toString())
                println(it)
            }
        }
    }

    fun subscribeMessageFromOther(): SharedFlow<ReceivedData> {
        return localServer.server
    }

}